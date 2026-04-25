package com.maheswara660.filora

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.Process
import android.widget.Toast
import androidx.annotation.StringRes
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.bitmapFactoryMaxParallelism
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.addLastModifiedToFileCacheKey
import coil3.request.allowConversionToBitmap
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import coil3.video.VideoFrameDecoder
import com.maheswara660.filora.coil.apk.ApkFileDecoder
import com.maheswara660.filora.coil.audio.AudioFileDecoder
import com.maheswara660.filora.coil.pdf.PdfFileDecoder
import com.maheswara660.filora.coil.zip.ZipFileFetcher
import com.maheswara660.filora.coil.zip.ZipFileKeyer
import com.maheswara660.filora.common.FileExplorerLogger
import com.maheswara660.filora.screen.main.MainActivityManager
import com.maheswara660.filora.screen.main.tab.files.coil.DocumentFileMapper
import com.maheswara660.filora.screen.main.tab.files.holder.LocalFileHolder
import com.maheswara660.filora.screen.main.tab.files.search.SearchManager
import com.maheswara660.filora.screen.main.tab.files.task.TaskManager
import com.maheswara660.filora.screen.main.tab.files.zip.ZipManager
import com.maheswara660.filora.screen.preferences.PreferencesManager
import com.maheswara660.filora.screen.textEditor.TextEditorManager
import com.maheswara660.filora.screen.viewer.ViewersManager
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

class App : Application(), coil3.SingletonImageLoader.Factory {
    companion object {
        lateinit var appContext: Context

        val globalClass
            get() = appContext as App

        val logger
            get() = globalClass.logger
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + IO)

    lateinit var logger: FileExplorerLogger
        private set

    val appFiles: LocalFileHolder
        get() = LocalFileHolder(
            File(Environment.getExternalStorageDirectory(), ".filora")
                .apply { if (!exists()) mkdirs() }
        )

    val recycleBinDir: LocalFileHolder
        get() = LocalFileHolder(
            File(
                appFiles.file,
                "bin"
            ).apply { mkdirs() })

    private var uid = AtomicInteger(0)

    val cleanOnExitDir
        get() = LocalFileHolder(
            File(
                getExternalFilesDir(null),
                "cleanOnExit"
            ).apply { mkdirs() }
        )

    val textEditorManager: TextEditorManager by lazy { TextEditorManager().also { setupTextMate() } }
    val mainActivityManager: MainActivityManager by lazy { MainActivityManager().also { it.setup() } }
    val preferencesManager: PreferencesManager by lazy { PreferencesManager() }
    val viewersManager: ViewersManager by lazy {
        setupTextMate()
        ViewersManager()
    }
    val taskManager: TaskManager by lazy { TaskManager() }
    val zipManager: ZipManager by lazy { ZipManager() }
    val searchManager: SearchManager by lazy { SearchManager() }

    val supportedAudioExtensions = listOf(
        "mp3", "ogg", "wav", "flac", "aac", "wma", "aiff", "amr", "m4a", "opus", "mka", "mid", "midi"
    )

    var pendingAudioPlaylist: List<File>? = null

    override fun onCreate() {

        super.onCreate()

        logger = FileExplorerLogger(this, applicationScope)
        setupGlobalExceptionHandler()

        appContext = this

        performCleanOnExit()
    }

    fun performCleanOnExit() {
        applicationScope.launch {
            if (cleanOnExitDir.file.exists()) {
                cleanOnExitDir.file.deleteRecursively()
            }
        }
    }

    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            logger.logError(exception)
            defaultHandler?.uncaughtException(thread, exception)
            Process.killProcess(Process.myPid())
            exitProcess(2)
        }
    }

    private fun setupTextMate() {
        applicationScope.launch {
            FileProviderRegistry.getInstance().addFileProvider(
                AssetsFileResolver(
                    appContext.assets
                )
            )

            GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")

            val themeRegistry = ThemeRegistry.getInstance()
            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry
                            .getInstance()
                            .tryGetInputStream("textmate/dark.json"),
                        "dark.json", null
                    ),
                    "dark"
                )
            )

            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry
                            .getInstance()
                            .tryGetInputStream("textmate/light.tmTheme"),
                        "light.tmTheme",
                        null
                    ),
                    "light"
                )
            )
        }
    }

    fun showMsg(@StringRes msgSrc: Int) {
        showMsg(getString(msgSrc))
    }

    fun showMsg(msg: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@App, msg, Toast.LENGTH_SHORT).show()
        }
    }


    fun generateUid() = uid.andIncrement

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .addLastModifiedToFileCacheKey(true)
            .crossfade(true)
            .allowConversionToBitmap(true)
            .coroutineContext(Dispatchers.Default)
            .interceptorCoroutineContext(Dispatchers.Default)
            .bitmapFactoryMaxParallelism(1)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.35)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .components {
                add(DocumentFileMapper())
                add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
                add(ApkFileDecoder.Factory())
                add(PdfFileDecoder.Factory())
                add(AudioFileDecoder.Factory())
                add(ZipFileFetcher.Factory())
                add(ZipFileKeyer())
            }
            .build()
    }
}
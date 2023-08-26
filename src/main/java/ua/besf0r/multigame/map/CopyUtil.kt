package ua.besf0r.multigame.map

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object CopyUtil{

    private fun copyFileStructure(source: File, target: File): CompletableFuture<Void> {
        val executor = Executors.newSingleThreadExecutor()

        val future = CompletableFuture.runAsync {
            try {
                val ignore = ArrayList(listOf("uid.dat", "session.lock", "raids.dat", "stats", "playerdata"))
                if (!ignore.contains(source.name)) {
                    if (source.isDirectory) {
                        if (!target.exists()) {
                            if (!target.mkdirs()) throw IOException("Couldn't create world directory!")
                        }
                        source.listFiles()?.forEach { file ->
                            val srcFile = File(source, file.name)
                            val destFile = File(target, file.name)
                            copyFileStructure(srcFile, destFile).join() // Очікуємо завершення копіювання папки
                        }
                    } else {
                        FileInputStream(source).use { `in` ->
                            FileOutputStream(target).use { out ->
                                val buffer = ByteArray(1024)
                                var length: Int
                                while (`in`.read(buffer).also { length = it } > 0) {
                                    out.write(buffer, 0, length)
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        future.whenComplete { _, _ -> executor.shutdown() }

        return future
    }

    fun copyWorld(originalWorld: File, newWorldName: String, worldContainer: File): CompletableFuture<Void> {
        val newWorldDir = File(worldContainer, newWorldName)
        return copyFileStructure(originalWorld, newWorldDir)
    }
}
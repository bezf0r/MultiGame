package ua.besf0r.multigame.map

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.*

class CopyUtil {
    private fun copyFileStructure(source: File, target: File) {
        try {
            val ignore = ArrayList(listOf("uid.dat", "session.lock", "raids.dat","stats","playerdata"))
            if (!ignore.contains(source.name)) {
                if (source.isDirectory) {
                    if (!target.exists()) if (!target.mkdirs()) throw IOException("Couldn't create world directory!")
                    val files = source.list()
                    for (file in files) {
                        val srcFile = File(source, file)
                        val destFile = File(target, file)
                        copyFileStructure(srcFile, destFile)
                    }
                } else {
                    val `in`: InputStream = FileInputStream(source)
                    val out: OutputStream = FileOutputStream(target)
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (`in`.read(buffer).also { length = it } > 0) out.write(buffer, 0, length)
                    `in`.close()
                    out.close()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
    fun copyWorld(originalWorld: World, newWorldName: String): World? {
        copyFileStructure(originalWorld.worldFolder, File(Bukkit.getWorldContainer(), newWorldName))
        return WorldCreator(newWorldName).createWorld()
    }
}
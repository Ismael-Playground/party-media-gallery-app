import java.util.Properties
import java.io.FileInputStream
import java.io.File

object VersionConfig {
    
    private fun getVersionProperties(): Properties {
        val props = Properties()
        val versionFile = File("version.properties")
        if (versionFile.exists()) {
            props.load(FileInputStream(versionFile))
        }
        return props
    }
    
    fun getBuildNumber(): String {
        // GitHub Actions
        System.getenv("GITHUB_RUN_NUMBER")?.let { return it }
        
        // GitLab CI
        System.getenv("CI_PIPELINE_IID")?.let { return it }
        
        // Jenkins
        System.getenv("BUILD_NUMBER")?.let { return it }
        
        // CircleCI
        System.getenv("CIRCLE_BUILD_NUM")?.let { return it }
        
        // Azure DevOps
        System.getenv("BUILD_BUILDNUMBER")?.let { 
            // Extract number from format like "20231201.1"
            return it.substringAfterLast(".")
        }
        
        // Default para builds locales
        return "local"
    }
    
    fun getBuildNumberNumeric(): String {
        val buildNum = getBuildNumber()
        return if (buildNum == "local") "999" else buildNum
    }
    
    fun getVersionName(): String {
        val props = getVersionProperties()
        val major = props.getProperty("major", "1")
        val patch = props.getProperty("patch", "0")
        val buildNum = getBuildNumber()
        
        // Minor es ahora el número de build, 0 para local
        val minor = if (buildNum == "local") "0" else buildNum
        
        return when {
            isReleaseBuild() -> "$major.$minor.$patch"  // Release: X.Y.Z
            else -> "$major.$minor.$patch-SNAPSHOT"  // Debug: X.Y.Z-SNAPSHOT
        }
    }
    
    fun getVersionNameForPackaging(): String {
        // Para Desktop packaging que requiere formato X.Y.Z estricto
        val props = getVersionProperties()
        val major = props.getProperty("major", "1")
        val patch = props.getProperty("patch", "0")
        val buildNum = getBuildNumber()
        
        // Minor es ahora el número de build, 0 para local
        val minor = if (buildNum == "local") "0" else buildNum
        
        return "$major.$minor.$patch"
    }
    
    fun getVersionCode(): Int {
        val props = getVersionProperties()
        val major = props.getProperty("major", "1").toInt()
        val patch = props.getProperty("patch", "0").toInt()
        val buildNum = getBuildNumber()
        
        // Minor es ahora el número de build, 0 para local
        val minor = if (buildNum == "local") 0 else buildNum.toInt()
        
        return major * 10000 + minor * 100 + patch
    }
    
    fun isReleaseBuild(): Boolean {
        // Es un release si hay un tag de Git que comience con 'v'
        val gitTag = getGitTag()
        return gitTag.isNotEmpty() && gitTag.startsWith("v")
    }
    
    private fun getGitTag(): String {
        return try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("git describe --exact-match --tags HEAD")
            process.inputStream.bufferedReader().readText().trim()
        } catch (e: Exception) {
            ""
        }
    }
    
    fun getBuildType(): String {
        return if (isReleaseBuild()) "release" else "debug"
    }
    
    fun getGitCommitHash(): String {
        return try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("git rev-parse --short HEAD")
            process.inputStream.bufferedReader().readText().trim()
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    fun getVersionInfo(): Map<String, String> {
        return mapOf(
            "versionName" to getVersionName(),
            "versionCode" to getVersionCode().toString(),
            "buildNumber" to getBuildNumber(),
            "buildType" to getBuildType(),
            "gitHash" to getGitCommitHash(),
            "buildTime" to java.time.Instant.now().toString()
        )
    }
}
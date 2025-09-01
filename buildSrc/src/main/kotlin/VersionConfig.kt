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
        val minor = props.getProperty("minor", "0")
        val patch = getBuildNumberForVersion()
        
        return "$major.$minor.$patch"
    }
    
    private fun getBuildNumberForVersion(): String {
        val buildNum = getBuildNumber()
        return if (buildNum == "local") "1" else buildNum
    }
    
    fun getVersionCode(): Int {
        val props = getVersionProperties()
        val major = props.getProperty("major", "1").toInt()
        val minor = props.getProperty("minor", "0").toInt()
        val patch = getBuildNumberForVersion().toInt()
        
        return if (getBuildNumber() == "local") {
            // Para builds locales: major * 10000 + minor * 100 + 1
            major * 10000 + minor * 100 + 1
        } else {
            // Para CI/CD: major * 10000 + minor * 100 + buildNumber
            major * 10000 + minor * 100 + patch
        }
    }
    
    fun isReleaseBuild(): Boolean {
        return System.getenv("CI") == "true" && getBuildNumber() != "local"
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
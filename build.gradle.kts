// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
}

tasks.register<Zip>("zipProject") {
    archiveFileName.set("amar_clothing_pos_project.zip")
    destinationDirectory.set(file("${rootDir}/APK_DOWNLOAD"))
    from(rootDir) {
        exclude(".gradle")
        exclude(".build-outputs")
        exclude("APK_DOWNLOAD")
        exclude("**/build")
        exclude(".idea")
        exclude(".git")
    }
}


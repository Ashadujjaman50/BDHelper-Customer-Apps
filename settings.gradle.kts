pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        //Solution in image slider
        //maven { url = uri("https://maven.scijava.org/content/repositories/public/") }

        maven { url = uri("https://jitpack.io") } // 👈 এটা যোগ করো
    }
}

rootProject.name = "BD Helper"
include(":app")

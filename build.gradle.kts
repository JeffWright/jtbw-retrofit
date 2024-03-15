plugins {
  kotlin("jvm") version "1.9.22"
  id("com.ncorti.ktfmt.gradle") version "0.11.0"
  `maven-publish`
}

group = "dev.jtbw.retrofit"

version = "1.0"

repositories { mavenCentral() }

dependencies {
  api(libs.retrofit)
  api(libs.coroutines.core)
  api(libs.retrofit.converter.moshi)
  api(libs.retrofit.converter.scalars)
  api(libs.okhttp.logging.interceptor)
  api(libs.moshi)
  api(libs.moshi.kotlin)
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(17) }

java {
  // Publish Sources
  withSourcesJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.github.JeffWright"
      version = "0.9.0"
      artifactId = "jtbw-retrofit"

      from(components["java"])
    }
  }
}

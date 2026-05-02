plugins {
	java
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.scndry"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven {
		name = "centralSnapshots"
		url = uri("https://central.sonatype.com/repository/maven-snapshots/")
		mavenContent { snapshotsOnly() }
	}
}

dependencies {
	implementation("io.github.scndry:jackson-dataformat-spreadsheet:1.5.0")
	implementation("com.h2database:h2:2.2.224")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// --- Visual fixture generation (maintainer ritual; not part of CI) ---
val visualFixturesDir = layout.buildDirectory.dir("visual-fixtures")

tasks.register<JavaExec>("generateVisualFixtures") {
	group = "verification"
	description = "Generate XLSX fixtures from sheet examples"
	classpath = sourceSets["test"].runtimeClasspath
	mainClass.set("io.github.scndry.examples.visualfixture.VisualFixtureGenerator")
	args(visualFixturesDir.get().asFile.absolutePath)
	outputs.dir(visualFixturesDir)
}

tasks.register("renderVisualFixtures") {
	group = "verification"
	description = "Render XLSX fixtures to PNG via LibreOffice (skipped if soffice not on PATH)"
	dependsOn("generateVisualFixtures")
	onlyIf {
		val available = try {
			ProcessBuilder("which", "soffice")
				.redirectOutput(ProcessBuilder.Redirect.DISCARD)
				.redirectError(ProcessBuilder.Redirect.DISCARD)
				.start()
				.waitFor() == 0
		} catch (_: Exception) {
			false
		}
		if (!available) logger.warn("soffice not on PATH — skipping. Install LibreOffice to render fixtures.")
		available
	}
	doLast {
		val outDir = visualFixturesDir.get().asFile
		val xlsxFiles = outDir.listFiles { f -> f.extension == "xlsx" }?.toList().orEmpty()
		if (xlsxFiles.isEmpty()) {
			logger.warn("No XLSX fixtures found in ${outDir.absolutePath}")
			return@doLast
		}
		exec {
			commandLine = listOf(
				"soffice",
				"--headless",
				"--norestore",
				"-env:UserInstallation=file:///tmp/libre-fixture-profile",
				"--convert-to", "png",
				"--outdir", outDir.absolutePath,
			) + xlsxFiles.map { it.absolutePath }
		}
	}
}

tasks.register("visualFixtures") {
	group = "verification"
	description = "Generate XLSX fixtures and render to PNG (XLSX always; PNG if soffice available)"
	dependsOn("renderVisualFixtures")
}

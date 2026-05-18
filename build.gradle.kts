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

// Override Spring Boot 3.5 BOM pin (3.17.0) to fix CVE-2025-48924
// (Apache Commons Lang < 3.18.0 — uncontrolled recursion).
extra["commons-lang3.version"] = "3.18.0"

dependencies {
	implementation("io.github.scndry:jackson-dataformat-spreadsheet:1.6.4")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
	implementation("com.h2database:h2:2.2.224")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.apache.commons:commons-fileupload2-jakarta-servlet6:2.0.0-M4")
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

fun resolveSoffice(): String? {
	// Well-known absolute paths cover Homebrew (Apple Silicon + Intel), system, and direct app bundle.
	// Resolved here instead of relying on PATH search inside Gradle's native process launcher.
	val candidates = listOf(
		"/opt/homebrew/bin/soffice",
		"/usr/local/bin/soffice",
		"/usr/bin/soffice",
		"/Applications/LibreOffice.app/Contents/MacOS/soffice",
	)
	return candidates.firstOrNull { File(it).canExecute() }
}

tasks.register("renderVisualFixtures") {
	group = "verification"
	description = "Render XLSX fixtures to PNG via LibreOffice (skipped if soffice not found)"
	dependsOn("generateVisualFixtures")
	val sofficePath = resolveSoffice()
	onlyIf {
		if (sofficePath == null) logger.warn("soffice not found at well-known paths — skipping. Install LibreOffice to render fixtures.")
		sofficePath != null
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
				sofficePath!!,
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

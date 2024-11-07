import java.util.Properties
import java.io.File

plugins {
    distribution
}

group = "io.sharptree"

version = "1.0.0"

val vendor = "Sharptree"
val product = "MAS Maximo Redirect"
val distro = "mas-maximo-redirect"

project.version = "1.0.0"

repositories {
    mavenCentral()
}

distributions {
    main {
        distributionBaseName.set(distro.toLowerCase())
    }
}

// Configure the distribution task to tar and gzip the results.
tasks.distTar {
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}

tasks.assemble {
    finalizedBy("fixzip")
}

tasks.register("fixzip") {
    dependsOn("rezip", "retar")

    doLast {
        delete(layout.buildDirectory.asFile.get().path + File.separator + "distributions" + File.separator + "tmp")
    }
}

tasks.register("unzip") {
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"
    val archiveBaseName = distro.toLowerCase() + "-" + project.version

    doLast {
        copy {
            from(zipTree(tasks.distZip.get().archiveFile.get().asFile))
            into(distDir + File.separator + "tmp" )
        }

        copy{
            from(distDir + File.separator + "tmp" + File.separator + archiveBaseName +  File.separator)
            into(distDir + File.separator + "tmp" + File.separator )
        }

        delete(distDir + File.separator + "tmp" + File.separator + archiveBaseName)
    }
}

tasks.register<Zip>("rezip") {
    dependsOn("unzip")
    val archiveBaseName = distro.toLowerCase() + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"
    val baseDir = File(distDir + File.separator + "tmp" )

    archiveFileName.set("$archiveBaseName.zip")

    from(baseDir) {
        exclude("tmp/")
        into("/")
    }
}

tasks.register<Tar>("retar") {
    dependsOn("unzip")
    val archiveBaseName = distro.toLowerCase() + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"
    val baseDir = File(distDir + File.separator + "tmp" )

    compression = Compression.GZIP
    archiveFileName.set("$archiveBaseName.tar.gz")
    archiveExtension.set("tar.gz")

    from(baseDir) {
        exclude("tmp/")
        into("/")
    }
}

tasks.getByName("unzip").dependsOn("assembleDist")

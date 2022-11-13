dependencies {
    implementation(project(":protocol"))

    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")

    compileOnly("me.func:atlas-api:1.0.10")
    compileOnly("me.func:visual-driver:3.2.8.RELEASE")

    compileOnly("dev.xdark:feder:1.0")

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "stronghold"
            from(components["java"])
        }
    }
}

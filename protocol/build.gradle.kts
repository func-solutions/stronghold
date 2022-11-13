dependencies {
    api("cristalix:microservice:20.11.04")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "stronghold-protocol"
            from(components["java"])
        }
    }
}

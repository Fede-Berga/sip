plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(project(":sip-domain"))

    testFixturesImplementation(project(":sip-domain"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

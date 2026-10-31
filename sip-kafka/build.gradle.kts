plugins {
    alias(libs.plugins.spring.dependency.management)
}
dependencies {
    implementation(project(":sip-domain"))
    implementation(project(":sip-application"))
    implementation(libs.spring.boot.starter.kafka)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.kafka)
}

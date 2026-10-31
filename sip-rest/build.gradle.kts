plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.0")
    }
}

dependencies {
    api(project(":sip-domain"))
    implementation(project(":sip-application"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson)
    implementation(libs.jackson.jsr310)
    testImplementation(libs.spring.boot.starter.test)
}
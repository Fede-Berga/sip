plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.2")
    }
}

dependencies {
    api(project(":sip-domain"))
    implementation(project(":sip-application"))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.redis)
    implementation(libs.liquibase)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    runtimeOnly("org.postgresql:postgresql")

    testFixturesImplementation(project(":sip-domain"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)

    implementation(libs.jackson)
    implementation(libs.jackson.jsr310)
}
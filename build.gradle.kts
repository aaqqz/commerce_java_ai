// root build.gradle.kts (루트 빌드 설정 및 플러그인 선언)
plugins {
    java
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

// allproject settings (모든 프로젝트(루트 + 서브프로젝트)에 적용되는 설정)
allprojects {
    group = "${property("projectGroup")}"
    version = "${property("applicationVersion")}"

    repositories {
        mavenCentral()
    }
}

// subproject settings (모든 서브프로젝트(하위 모듈)에만 적용되는 설정)
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudDependenciesVersion")}")
        }
    }

    dependencies {
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

        testImplementation("org.springframework.boot:spring-boot-starter-test")

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = true
    }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")
    }
    tasks.withType<JavaCompile> {
        options.release.set(project.property("javaVersion").toString().toInt())
    }

    tasks.named<Test>("test") {
        useJUnitPlatform {
            excludeTags("develop") // 기본 test 실행 시 develop 태그가 붙은 테스트는 제외(개발용 테스트를 기본 실행에서 제외)
        }
    }

    tasks.register<Test>("unitTest") {
        group = "verification"
        useJUnitPlatform {
            excludeTags("develop", "context")
        }
    }

    tasks.register<Test>("contextTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("context")
        }
    }

    tasks.register<Test>("developTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("develop")
        }
    }
}

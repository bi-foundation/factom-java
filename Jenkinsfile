node {
    stage 'Checkout sources'
        checkout scm

    stage 'Build'
        echo "Branch is: ${env.BRANCH_NAME}"
        sh "bash ./gradlew --stacktrace build -PBUILD_NUMBER=${env.BUILD_NUMBER}"

}
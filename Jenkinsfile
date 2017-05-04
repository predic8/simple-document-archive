#!groovy
def containerName = "document-archive-build"
def tempImageName = "p8/document-archive-build"

@NonCPS
String getProxies(String line) {
  match = line =~ /\{file:(.*)\}/
  String result = match ? match.group(1) : null
  match = null
  result
}

stage 'Checkout'

node {
  checkout scm
  stash 'src'
}

stage 'Build'

node {
  unstash 'src'
   
  withCredentials(
    [
      [
        $class: 'FileBinding', 
        credentialsId: 'kubernetes-ca.pem', 
        variable: 'KUBERNETES_CA'
      ], 
      [
        $class: 'FileBinding', 
        credentialsId: 'kubernetes-config', 
        variable: 'KUBERNETES_CONFIG'
      ], 
      [
        $class: 'FileBinding', 
        credentialsId: 'kubernetes-jenkins.pem', 
        variable: 'KUBERNETES_JENKINS'
      ], 
      [
        $class: 'FileBinding', 
        credentialsId: 'kubernetes-jenkins-key.pem', 
        variable: 'KUBERNETES_JENKINS_KEY'
      ]
    ]
  ) {
      String text = sh returnStdout: true, script: "cat $KUBERNETES_CA"
      writeFile file: ".kube/ca.pem", text: text
      text = sh returnStdout: true, script: "cat $KUBERNETES_CONFIG"
      writeFile file: ".kube/config", text: text
      text = sh returnStdout: true, script: "cat $KUBERNETES_JENKINS"
      writeFile file: ".kube/jenkins.pem", text: text
      text = sh returnStdout: true, script: "cat $KUBERNETES_JENKINS_KEY"
      writeFile file: ".kube/jenkins-key.pem", text: text
   }

   sh "docker build -t $tempImageName ."
   withCredentials(
    [
      [
        $class: 'UsernamePasswordMultiBinding', 
        credentialsId: 'c9f674a1-9ede-4624-9fe2-ff600491e28c', 
        passwordVariable: 'HUB_PASSWORD', 
        usernameVariable: 'HUB_USER'
      ],
      [
        $class: 'FileBinding',
        credentialsId: 'da-application-properties',
        variable: 'APPLICATION_PROPERTIES'
      ]
    ]
  ) {
      sh "docker ps -a | grep $containerName | awk '{print \$1}' | xargs --no-run-if-empty docker rm -f"
      sh 'export PROPERTIES="$(cat ${APPLICATION_PROPERTIES})"'
      sh "docker run --name $containerName -d -v /var/run/docker.sock:/var/run/docker.sock -e HUB_USER=$HUB_USER -e HUB_PASSWORD=$HUB_PASSWORD -e APPLICATION_PROPERTIES=$PROPERTIES $tempImageName sleep 1000000"
   }
}


def stageTasks = [ 'createDockerfile', 'dockerHubLogin', 'createDeploymentDescriptor','dockerImage', 'dockerImageTag', 'dockerPushImage', 'apply' ]

for (int i=0; i < stageTasks.size(); i++) {
 stage(stageTasks[i]) {
   node {
     def cmd
     cmd = "docker exec $containerName gradle --stacktrace"
     for (int j = 0; j < i; j++) {
       cmd += " -x " + stageTasks[j]
     }
     cmd += " " + stageTasks[i]
     sh "$cmd"
   }
 }
}

stage("cleanup") {
  node {
    sh "docker rm -f $containerName"
  }
}

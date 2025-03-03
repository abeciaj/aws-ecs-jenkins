import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def folderName = 'test-folder1'
def jobName = 'test-ijob1'
def BITBUCKET_REPO = 'https://abeciaj23@bitbucket.org/devopsph/hello-world.git'
def GITHUB_REPO = 'https://github.com/abeciaj/hello-world.git'

// Get Jenkins instance
def jenkins = Jenkins.get()

// Get or create the folder
def folder = jenkins.getItem(folderName)
if (folder == null) {
    folder = new Folder(jenkins, folderName)
    jenkins.add(folder, folderName)  // Corrected line
    jenkins.save()
    println "Folder '${folderName}' created."
} else {
    println "Folder '${folderName}' already exists."
}

// Get or create the pipeline job inside the folder
def job = folder.getItem(jobName)
if (job == null) {
    job = new WorkflowJob(folder, jobName)
    folder.add(job, jobName)  // Corrected line
    jenkins.save()
    println "Pipeline job '${jobName}' created inside folder '${folderName}'."
} else {
    println "Pipeline job '${jobName}' already exists inside folder '${folderName}'. Updating script..."
}

// Define the GitHub-to-Bitbucket synchronization pipeline script
def pipelineScript = """
pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS = 'github-token'
        BITBUCKET_CREDENTIALS = 'bitbucket-jenkins'  // Make sure this matches Jenkins credentials ID
    }

    stages {
        stage('Setup Environment') {
            steps {
                sh 'git config --global --add safe.directory $(pwd)'
            }
        }

        stage('Checkout GitHub') {
            steps {
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh '''
                        if [ -d ".git" ]; then
                            echo "Repository already exists. Pulling latest changes..."
                            git reset --hard
                            git clean -fd
                            git pull origin master
                        else
                            echo "Cloning repository..."
                            git clone https://$GITHUB_TOKEN@github.com/abeciaj/hello-world.git .
                            git checkout master
                        fi
                    '''
                }
            }
        }

        stage('Sync to Bitbucket') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'bitbucket-jenkins', usernameVariable: 'BITBUCKET_USER', passwordVariable: 'BITBUCKET_PASS')]) {
                    sh '''
                        echo "Adding Bitbucket remote..."
                        if git remote | grep -q bitbucket; then
                            echo "Bitbucket remote exists. Updating..."
                            git remote remove bitbucket
                        fi
                        
                        git remote add bitbucket https://$BITBUCKET_USER:$BITBUCKET_PASS@bitbucket.org/devopsph/hello-world.git
                        git config --global user.name "Jenkins"
                        git config --global user.email "jenkins@example.com"
                        
                        echo "Pulling changes from Bitbucket..."
                        git fetch bitbucket
                        git pull bitbucket master --rebase
                        
                        echo "Pushing to Bitbucket..."
                        git push -u bitbucket master
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for errors.'
        }
    }
}
"""

// Assign the pipeline script to the job
def flowDefinition = new CpsFlowDefinition(pipelineScript, true)
job.setDefinition(flowDefinition)
job.save()

println "Pipeline script added/updated for job '${jobName}' inside folder '${folderName}'."
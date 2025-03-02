import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.*
import jenkins.plugins.git.*

// Define folder and job names
def folderName = 'test-folder1'
def jobName = 'test-ijob1'
def gitUrl = 'https://github.com/abeciaj/hello-world.git'
def branch = 'main' // Change this if your branch is different

// Get Jenkins instance
def jenkins = Jenkins.instance

// Get or create the folder
def folder = jenkins.getItem(folderName)
if (folder == null) {
    folder = jenkins.createProject(Folder, folderName)
    println "Folder '${folderName}' created."
} else {
    println "Folder '${folderName}' already exists."
}

// Get or create the pipeline job inside the folder
def job = folder.getItem(jobName)
if (job == null) {
    job = folder.createProject(WorkflowJob, jobName)
    println "Pipeline job '${jobName}' created inside folder '${folderName}'."
} else {
    println "Pipeline job '${jobName}' already exists inside folder '${folderName}'. Updating settings..."
}

// Define GitHub repository as the SCM source
def scm = new GitSCM(
    [new UserRemoteConfig(gitUrl, null, null, null)], // Repo URL
    [new BranchSpec(branch)], // Branch
    false,
    [],
    null,
    null,
    [] // Additional behaviours
)

// Set the pipeline to use Jenkinsfile from GitHub
def scmFlowDefinition = new CpsScmFlowDefinition(scm, "Jenkinsfile") // Looks for 'Jenkinsfile' in the repo
job.definition = scmFlowDefinition

// Save the job
job.save()
println "Pipeline job '${jobName}' in folder '${folderName}' is now connected to GitHub repo '${gitUrl}'."

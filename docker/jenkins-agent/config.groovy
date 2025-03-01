import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*

def folderName = 'test-folder1'
def jobName = 'test-ijob1'

// Get the folder, create if not exists
def jenkins = Jenkins.instance
def folder = jenkins.getItem(folderName) ?: jenkins.createProject(Folder, folderName)

// Create the job inside the folder if it doesn't exist
if (folder.getItem(jobName) == null) {
    def job = folder.createProject(hudson.model.FreeStyleProject, jobName)
    println "Job '${jobName}' created inside folder '${folderName}'."
} else {
    println "Job '${jobName}' already exists inside '${folderName}'."
}

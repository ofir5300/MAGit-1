package Engine.GsonClasses;


import java.util.LinkedList;

public class PullRequestData {
    private String id;
    private String author;
    private String date;
    private String repositoryName;
    private String targetBranch;
    private String baseBranch;
    private String description;
    private String status;
    private String statusLogString;
    private LinkedList<CommitData> commitsDataList;

    public PullRequestData(String id, String author, String date, String repositoryName, String targetBranch, String baseBranch, String description, String statusLogString, LinkedList<CommitData> commitsDataList) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.repositoryName = repositoryName;
        this.targetBranch = targetBranch;
        this.baseBranch = baseBranch;
        this.description = description;
        this.status = "open";
        this.statusLogString = statusLogString;
        this.commitsDataList = commitsDataList;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public String getBaseBranch() {
        return baseBranch;
    }

    public String getDesctiption() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void approvePR() {
        this.status = "approved";
    }

    public void declinePR() {
        this.status = "declined";
    }
}

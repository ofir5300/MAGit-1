package app;

import Engine.*;
import Engine.Commons.CollaborationSource;
import body.BodyController;
import com.sun.org.apache.xpath.internal.operations.Bool;
import footer.FooterController;
import header.HeaderController;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import subComponents.checkOutDialog.CheckOutDialogController;
import subComponents.cloneDialog.CloneDialogController;
import subComponents.conflictsDialog.ConflictsDialogController;
import subComponents.conflictsDialog.conflictSolverDialog.ConflictSolverDialogController;
import subComponents.createNewBranchDialog.CreateNewBranchDialogController;
import subComponents.createNewBranchDialog.createRTBDialog.CreateRTBDialogController;
import subComponents.deleteBranchDialog.DeleteBranchDialogController;
import subComponents.mergeDialog.MergeDialogController;

import java.awt.TextArea;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AppController {

    // model & view
    private Manager model;
    private Stage view;

    // sub components
    @FXML private AnchorPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private AnchorPane bodyComponent;
    @FXML private BodyController bodyComponentController;
    @FXML private AnchorPane footerComponent;
    @FXML private FooterController footerComponentController;

    // dialog componenets
    @FXML private CreateNewBranchDialogController createNewBranchDialogController;
    private Scene createNewBranchDialogScene;
    @FXML private MergeDialogController mergeDialogController;
    private Scene mergeDialogScene;
    @FXML private ConflictsDialogController conflictsDialogController;
    private Scene conflictsDialogScene;
    @FXML private ConflictSolverDialogController conflictSolverDialogController;
    private Scene conflictSolverDialogScene;
    @FXML private CloneDialogController cloneDialogController;
    private Scene cloneDialogScene;
    @FXML private CreateRTBDialogController createRTBDialogController;
    private Scene createRTBDialogScene;
    @FXML private DeleteBranchDialogController deleteBranchDialogController;
    private Scene deleteBranchDialogScene;
    @FXML private CheckOutDialogController checkOutDialogController;
    private Scene checkOutDialogScene;

    // properties
    private SimpleStringProperty repoPath;
    private SimpleStringProperty repoName;
    private SimpleStringProperty remoteRepoPath;
    private SimpleStringProperty remoteRepoName;
    private SimpleStringProperty activeUser;
    private SimpleStringProperty headBranch;
    private SimpleBooleanProperty isRepositoryLoaded;
    private SimpleBooleanProperty isRemoteRepositoryExists;
    private SimpleBooleanProperty isUncommitedChanges;
    private SimpleBooleanProperty isMergeFinished;
//    private SimpleListProperty<Branch> branches;

    public AppController() {
        // initilizing properties
        repoPath = new SimpleStringProperty("No repository loded");
        repoName = new SimpleStringProperty("No repository loded");
        remoteRepoName = new SimpleStringProperty("No Remote repository loded");
        remoteRepoPath = new SimpleStringProperty("No Remote repository loded");
        activeUser = new SimpleStringProperty("Active user");
        headBranch = new SimpleStringProperty("HEAD");
        isRepositoryLoaded = new SimpleBooleanProperty(false);
        isRemoteRepositoryExists = new SimpleBooleanProperty(false);
        isUncommitedChanges = new SimpleBooleanProperty(false);
        isMergeFinished = new SimpleBooleanProperty(true);
    }

    // setters
    public void setHeaderComponentController(HeaderController headerController) { this.headerComponentController = headerController; }
    public void setBodyComponentController(BodyController bodyController) { this.bodyComponentController = bodyController; }
    public void setFooterComponentController(FooterController footerController) { this.footerComponentController = footerController; }
    public void setView(Stage view) { this.view = view; }
    public void setModel(Manager model) {
        this.model = model;
        activeUser.set(model.getActiveUser());
    }

    // getters
    public Manager getModel() {
        return this.model;
    }
    public BodyController getBodyComponentController() { return this.bodyComponentController ;}
    public DeleteBranchDialogController getDeleteBranchDialogController() { return deleteBranchDialogController; }
    public SimpleStringProperty getActiveUser() { return this.activeUser; }
    public SimpleStringProperty getRepoPath() { return this.repoPath; }
    public SimpleStringProperty getRepoName() {
        return this.repoName;
    }
    public SimpleStringProperty getRemoteRepoPath() { return this.remoteRepoPath; }
    public SimpleStringProperty getRemoteRepoName() { return this.remoteRepoName; }
    public SimpleStringProperty getHeadBranch() {return this.headBranch; }
    public SimpleBooleanProperty getIsRemoteRepositoryExists() { return this.isRemoteRepositoryExists; }
    public SimpleBooleanProperty getIsRepositoryLoaded() { return this.isRepositoryLoaded; }
    public SimpleBooleanProperty getIsUncommitedChanges() {
        return this.isUncommitedChanges;
    }
    public SimpleBooleanProperty getIsMergeFinished() {return this.isMergeFinished; }
    public Scene getConflictSolverDialogScene() { return this.conflictSolverDialogScene; }
    public Stage getView() { return this.view; }

    @FXML
    private void initialize() throws IOException {
        if(headerComponentController != null && bodyComponentController != null && footerComponentController != null) {
            headerComponentController.setAppController(this);
            bodyComponentController.setAppController(this);
            footerComponentController.setAppController(this);
        }

        initializeDialogComponents();
        bindSubComponentsProperties();

//        bodyComponentController.showCommitTree();
    }

    private void bindSubComponentsProperties() {
        headerComponentController.bindProperties();
        bodyComponentController.bindProperties();
        createNewBranchDialogController.bindProperties();
        mergeDialogController.bindProperties();
        conflictsDialogController.bindProperties();
        conflictSolverDialogController.bindProperties();
        cloneDialogController.bindProperties();
        createRTBDialogController.bindProperties();
        deleteBranchDialogController.bindProperties();
        checkOutDialogController.bindProperties();
    }   // footer??

    private void initializeDialogComponents() {
        FXMLLoader loader;

        try {
            // load new branch controller
            loader = new FXMLLoader();
            URL createNewBranchDialogFXML = getClass().getResource("/subComponents/createNewBranchDialog/createNewBranchDialog.fxml");
            loader.setLocation(createNewBranchDialogFXML);
            AnchorPane dialogRoot = loader.load();
            createNewBranchDialogScene = new Scene(dialogRoot);
            createNewBranchDialogController = loader.getController();
            createNewBranchDialogController.setMainController(this);

            // load merge controller
            loader = new FXMLLoader();
            URL mergeDialogFXML = getClass().getResource("/subComponents/mergeDialog/mergeDialog.fxml");
            loader.setLocation(mergeDialogFXML);
            AnchorPane mergeDialogRoot = loader.load();
            mergeDialogScene = new Scene(mergeDialogRoot);
            mergeDialogController = loader.getController();
            mergeDialogController.setMainController(this);

            // load conflicts controller
            loader = new FXMLLoader();
            URL conflictsDialogFXML = getClass().getResource("/subComponents/conflictsDialog/conflictsDialog.fxml");
            loader.setLocation(conflictsDialogFXML);
            AnchorPane conflictsDialogRoot = loader.load();
            conflictsDialogScene = new Scene(conflictsDialogRoot);
            conflictsDialogController = loader.getController();
            conflictsDialogController.setMainController(this);

            //  load conflictSolver controller
            loader = new FXMLLoader();
            URL conflictSolverDialogFXML = getClass().getResource("/subComponents/conflictsDialog/conflictSolverDialog/conflictSolverDialog.fxml");
            loader.setLocation(conflictSolverDialogFXML);
            AnchorPane conflictSolverDialogRoot = loader.load();
            conflictSolverDialogScene = new Scene(conflictSolverDialogRoot);
            conflictSolverDialogController = loader.getController();
            conflictSolverDialogController.setMainController(this);

            //  load clone controller
            loader = new FXMLLoader();
            URL cloneDialogFXML = getClass().getResource("/subComponents/cloneDialog/CloneDialog.fxml");
            loader.setLocation(cloneDialogFXML);
            AnchorPane cloneDialogRoot = loader.load();
            cloneDialogScene = new Scene(cloneDialogRoot);
            cloneDialogController = loader.getController();
            cloneDialogController.setMainController(this);

            //  load create RTB controller
            loader = new FXMLLoader();
            URL createRTBDialogFXML = getClass().getResource("/subComponents/createNewBranchDialog/createRTBDialog/CreateRTBDialog.fxml");
            loader.setLocation(createRTBDialogFXML);
            AnchorPane createRTBDialogRoot = loader.load();
            createRTBDialogScene = new Scene(createRTBDialogRoot);
            createRTBDialogController = loader.getController();
            createRTBDialogController.setMainController(this);

            //  load delete branch dialog controller
            loader = new FXMLLoader();
            URL deleteBranchDialogFXML = getClass().getResource("/subComponents/deleteBranchDialog/deleteBranchDialog.fxml");
            loader.setLocation(deleteBranchDialogFXML);
            AnchorPane deleteBranchDialogRoot = loader.load();
            deleteBranchDialogScene = new Scene(deleteBranchDialogRoot);
            deleteBranchDialogController = loader.getController();
            deleteBranchDialogController.setMainController(this);

            // load checkOut dialog controller
            loader = new FXMLLoader();
            URL checkOutDialogFXML = getClass().getResource("/subComponents/checkOutDialog/checkOutDialog.fxml");
            loader.setLocation(checkOutDialogFXML );
            AnchorPane checkOutDialogRoot = loader.load();
            checkOutDialogScene = new Scene(checkOutDialogRoot);
            checkOutDialogController = loader.getController();
            checkOutDialogController.setMainController(this);
        } catch (IOException e) {
            showExceptionDialog(e);
        }
    }

    @FXML
    public void cloneDialog() {
        checkForUncommitedChanges();
        Stage stage = new Stage();
        stage.setTitle("Clone");
        stage.setScene(cloneDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        bodyComponentController.expandAccordionTitledPane("remote");
    }

    @FXML
    public void createNewBranchDialog() {
        checkForUncommitedChanges();
        Stage stage = new Stage();
        stage.setTitle("Create new branch");
        stage.setScene(createNewBranchDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        bodyComponentController.expandAccordionTitledPane("branches");
    }

    @FXML
    public void deleteBranchDialog() {
        Stage stage = new Stage();
        stage.setTitle("Delete Branch");
        stage.setScene(deleteBranchDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        bodyComponentController.expandAccordionTitledPane("branches");
    }

    @FXML
    public void mergeDialog() {
        checkForUncommitedChanges();
        Stage stage = new Stage();
        stage.setTitle("Merge");
        stage.setScene(mergeDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        bodyComponentController.expandAccordionTitledPane("branches");
    }

    @FXML
    public void conflictsDialog() {
        Stage stage = new Stage();
        stage.setTitle("Conflicts");
        stage.setScene(conflictsDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void conflictSolverDialog() {
        Stage stage = new Stage();
        stage.setTitle("Conflict Solver");
        stage.setScene(conflictSolverDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void createRTBDialog() {
        Stage stage = new Stage();
        stage.setTitle("create Remote Tracking Branch");
        stage.setScene(createRTBDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void checkoutDialog() {
        Stage stage = new Stage();
        stage.setTitle("Checkout To Other Branch");
        stage.setScene(checkOutDialogScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void solveConflict(MergeConflict conflict, List<MergeConflict> conflicts) {
        updateConflictSolverContent(conflict);
        conflictSolverDialog();
    }

    @FXML//
    public void merge(Branch theirBranch) {
        try {
            Folder tree = new Folder();
            LinkedList<MergeConflict> conflicts = new LinkedList<MergeConflict>();
            LinkedList<MergeConflict> solvedConflicts = new LinkedList<MergeConflict>();
            ArrayList<LinkedList<MergeConflict>> conflicsLists = new ArrayList<LinkedList<MergeConflict>>();

            model.merge(theirBranch, tree, conflicts);
            while(!conflicts.isEmpty()) {
                updateConflictsList(conflicts, solvedConflicts);
                conflictsDialog();
                conflicsLists = updateConflictsList(conflicts, solvedConflicts);
                conflicts = conflicsLists.get(0);
                solvedConflicts = conflicsLists.get(1);
                if(!isMergeFinished.get()) {
                    break;
                }
            }

            if(isMergeFinished.get()) {
                model.mergeUpdateWC(tree, conflicsLists.size() == 0 ? null : conflicsLists.get(1));
                commit(theirBranch);
            } else {
                bodyComponentController.setTextAreaString("Merge process has been cancled. No changes has been made");
                bodyComponentController.selectTabInBottomTabPane("log");
            }
            isMergeFinished.set(true);
        } catch (WriteAbortedException e) {
            bodyComponentController.setTextAreaString(e.getMessage());
            bodyComponentController.selectTabInBottomTabPane("log");
        } catch (Exception e) {
            showExceptionDialog(e);
        }
        bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
        updateRepositoryUIAndDetails();
    }

    @FXML
    public void createNewBranch(String branchName, boolean shouldCheckout) {
        try {
            model.createNewBranch(branchName);
        } catch (Exception e) {
            showExceptionDialog(e);
        }

        if (shouldCheckout) {
            try {
                model.checkout(branchName);
                headBranch.set(model.getActiveRepository().getHEAD().getName());
            } catch (Exception e) {
                showExceptionDialog(e);
            }
        }
        bodyComponentController.setTextAreaString("Branch " + branchName + " created succesdully");
        bodyComponentController.selectTabInBottomTabPane("log");
        updateRepositoryUIAndDetails();
    }

    @FXML//
    public void deleteBranch(String branch) {
        try {
            model.deleteBranch(branch);
            bodyComponentController.setTextAreaString("Branch " + branch + " deleted succesdully");
            bodyComponentController.selectTabInBottomTabPane("log");
        }catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    @FXML
    public void commit() {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Commit");
        dialog.setHeaderText("Creating new commit");
        dialog.setContentText("Please enter your commit messsage:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(commitMessage -> {
            try {
                if(model.buildWorkingCopyTree() == null) {
                    throw new Exception("There is nothing to commit! Working Copy is empty");
                }
                StatusLog statusLog = model.commit(commitMessage);
                bodyComponentController.setTextAreaString(statusLog.toString());
                bodyComponentController.selectTabInBottomTabPane("log");
            } catch (Exception e) {
                showExceptionDialog(e);
            }
        });

        bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
        updateRepositoryUIAndDetails();
    }

    @FXML
    public void commit(Branch TheirsBranchMERGE) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Commit");
        dialog.setHeaderText("Creating new commit");
        dialog.setContentText("Please enter your commit messsage:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(commitMessage -> {
            try {
                StatusLog [] statusLog = model.commit(commitMessage, TheirsBranchMERGE.getCommit());
                bodyComponentController.setTextAreaString("Delta related to Head Branch:\n"
                                                            + statusLog[0].toString()
                                                             + "Delta related to " + TheirsBranchMERGE.getName() + " Branch: \n"
                                                               +  statusLog[1].toString());
                bodyComponentController.selectTabInBottomTabPane("log");
            } catch (IOException e) {
                showExceptionStackTraceDialog(e);
            }
        });

        bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
        updateRepositoryUIAndDetails();
    }

    @FXML
    public void showStatus() {
        try {
            String repoStatus = model.showStatus().toString();
            bodyComponentController.setTextAreaString(repoStatus);
            bodyComponentController.selectTabInBottomTabPane("log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void checkout(String branchName) {
        try {
            checkForUncommitedChanges();
            if (isUncommitedChanges.get()) {
                if (yesNoCancelDialog(
                        "Uncommited changes",
                        "There are uncommited changes\nSome data may be lost!",
                        "Checkout anyway?"
                )) {
                    model.checkout(branchName);
                } else {
                    bodyComponentController.setTextAreaString("checkout cancelled");
                    bodyComponentController.selectTabInBottomTabPane("log");
                }
            } else {
                model.checkout(branchName);
            }
            headBranch.set(model.getActiveRepository().getHEAD().getName());
        } catch (Exception e) {
            showExceptionDialog(e);
        }

        bodyComponentController.expandAccordionTitledPane("branches");
        bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());

        updateRepositoryUIAndDetails();
    }

    @FXML
    public void switchActiveUser() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Change active user");
        dialog.setHeaderText("The active user is: " + model.getActiveUser());
        dialog.setContentText("Please enter new user name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            model.switchUser(result.get());
            activeUser.set(result.get());
        }
    }

    @FXML
    public void switchRepository() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository to load");
        File selectedFolder = directoryChooser.showDialog(view);

        if (selectedFolder == null) {
            return;
        }
        Path absolutePath = Paths.get(selectedFolder.getAbsolutePath());
        try{
            model.switchRepository(absolutePath);
            isRepositoryLoaded.set(true);
            if(model.getActiveRepository().getCollaborationSource().equals(CollaborationSource.REMOTE)) {
                isRemoteRepositoryExists.set(true);
            } else {
                isRemoteRepositoryExists.set(false);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error loading repository");
            alert.setHeaderText(ex.getMessage());
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeCancel);
            alert.showAndWait();
            isRepositoryLoaded.set(false);
        }

        if(isRepositoryLoaded.get()) {
            bodyComponentController.expandAccordionTitledPane("repository");
            bodyComponentController.setTextAreaString("Repository switched successfully\nActive repository: " + model.getActiveRepository().getName());
            bodyComponentController.selectTabInBottomTabPane("log");
            bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
            headBranch.set(model.getActiveRepository().getHEAD().getName());
            updateRepositoryUIAndDetails();
        }
    }

    @FXML
    public void createNewRepository() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Repository");
        dialog.setHeaderText("Setting new Repository");
        dialog.setContentText("Please enter new Repository name:");
        Optional<String> result = dialog.showAndWait();
        String repositoryName = result.get();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select new repository url path");
        File selectedFolder = directoryChooser.showDialog(view);

        if (selectedFolder == null) {
            return;
        }
        Path absolutePath = Paths.get(selectedFolder.getAbsolutePath());
        try{
            model.createNewRepository(absolutePath, repositoryName);
            isRepositoryLoaded.set(true);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Existing repository");
            alert.setHeaderText(ex.getMessage());
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeCancel);
            alert.showAndWait();
            isRepositoryLoaded.set(false);
        }

        if(isRepositoryLoaded.get()) {
            bodyComponentController.expandAccordionTitledPane("repository");
            bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
            bodyComponentController.setTextAreaString("Repository created successfully\nActive repository: " + model.getActiveRepository().getName());
            bodyComponentController.selectTabInBottomTabPane("log");
            headBranch.set(model.getActiveRepository().getHEAD().getName());
            updateRepositoryUIAndDetails();
        }
    }

    @FXML
    public void push() {
        try {
            model.push();
            bodyComponentController.setTextAreaString("Push success\nbranch " + model.getActiveRepository().getHEAD().getName() + " is now up to date both locally and remotely");
            bodyComponentController.selectTabInBottomTabPane("log");
        } catch (Exception e) {
            showExceptionDialog(e);
        }
    }


    @FXML//
    public void pull() {
        try{
            if (isUncommitedChanges.get()) {
                if (yesNoCancelDialog(
                        "Uncommited changes",
                        "There are uncommited changes\nSome data may be lost!",
                        "Pull anyway?"
                )) {
                    this.merge(model.pull());
                } else {
                    bodyComponentController.setTextAreaString("Pull cancelled");
                    bodyComponentController.selectTabInBottomTabPane("log");
                }
            } else {
                this.merge(model.pull());
            }
            bodyComponentController.setTextAreaString("Pull success\nhead branch " + model.getActiveRepository().getHEAD().getName() + " is now up to date with Remote Branch" );
            bodyComponentController.selectTabInBottomTabPane("log");
            updateRepositoryUIAndDetails();
        } catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    @FXML//
    public void fetch() {
        try{
            model.fetch();
            bodyComponentController.setTextAreaString("Fetch success\nAll Remote Branches are up to date");
            bodyComponentController.selectTabInBottomTabPane("log");
            updateRepositoryUIAndDetails();
        } catch (Exception e) {
            showExceptionDialog(e);
        }

        updateRepositoryUIAndDetails();
    }

    @FXML
    public void importRepositoryFromXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(view);
        if (selectedFile == null) {
            return;
        }
        Path absolutePath = Paths.get(selectedFile.getAbsolutePath());
        try {
            model.importFromXML(absolutePath, false);
            isRepositoryLoaded.set(true);
        } catch (ObjectAlreadyActive e) {
            if (yesNoCancelDialog(
                    "Existing repository",
                    "There is already an existing repository in the XML's path",
                    "Do you want to overwrite it?"
            )){
                try {
                    model.importFromXML(absolutePath, true);
                    isRepositoryLoaded.set(true);
                    if(model.getActiveRepository().getCollaborationSource().equals(CollaborationSource.REMOTE)) {
                        isRemoteRepositoryExists.set(true);
                    } else {
                        isRemoteRepositoryExists.set(false);
                    }
                } catch (Exception ex) {
                    isRepositoryLoaded.set(false);
                    showExceptionDialog(ex);
                }
            } else {
                isRepositoryLoaded.set(false);
            }

            if(isRepositoryLoaded.get()) {
                bodyComponentController.expandAccordionTitledPane("repository");
                bodyComponentController.setTextAreaString("Repository imported successfully\nActive repository: " + model.getActiveRepository().getName());
                bodyComponentController.selectTabInBottomTabPane("log");
                headBranch.set(model.getActiveRepository().getHEAD().getName());
                updateRepositoryUIAndDetails();
            }
        } catch (Exception ex) {
            showExceptionStackTraceDialog(ex);
        }
    }

    public void updateRepositoryUIAndDetails() {
        if(isRepositoryLoaded.get()) {
            Repository activeRepository = model.getActiveRepository();
            repoPath.set(activeRepository.getRootPath().toString());
            repoName.set(activeRepository.getName()
                    + (isRemoteRepositoryExists.get() == true ? " (Local)" : ""));
            headBranch.set(model.getActiveRepository().getHEAD().getName());

            if(isRemoteRepositoryExists.get()) {
                remoteRepoPath.set(activeRepository.getRemotePath().toString());
                remoteRepoName.set(activeRepository.getName());
            }

            updateBranchesButtons();
            try {
                bodyComponentController.showCommitTree();
            } catch (IOException e) {
                this.showExceptionStackTraceDialog(e);
            }
        }

        try {
            bodyComponentController.showCommitTree();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBranchesButtons() {
        createBranchesSideCheckoutButtons();
        updateBranchButtons();
    }

    private void createBranchesSideCheckoutButtons() {
        if(isRepositoryLoaded.get()) {
            Repository activeRepository = model.getActiveRepository();
            HashSet<Branch> branchesSet = activeRepository.getBranches();
            bodyComponentController.getBranchesButtonsVBox().getChildren().clear();
            for (Branch branch : branchesSet) {
                String branchName;
                Button branchButton = new Button();

                if(branch.getCollaborationSource().equals(CollaborationSource.REMOTE)) {
                    branchName = activeRepository.getName() + File.separator + branch.getName();
                    branchButton.setText(branchName);

                    if(checkIfRTB(branch.getName())) {
                        branchButton.setOnAction(event -> {
                            try {
                                this.checkout(branch.getName());
                                headBranch.set(model.getActiveRepository().getHEAD().getName());
                                updateBranchesSideCheckoutButtons();
                                updateBranchButtons();
                            } catch (Exception e) {
                                showExceptionDialog(e);
                            }
                        });
                    } else {
                        branchButton.setOnAction(event -> {
                            ChoiceBox branchChooserChoiceBox = createRTBDialogController.getBranchesChoiceBox();
                            branchChooserChoiceBox.setValue(branch);
                            createRTBDialog();
                        });
                    }
                } else {
                    branchName = branch.getName();
                    branchButton.setText(branchName);
                    branchButton.setOnAction(event -> {
                        try {
                            this.checkout(branchName);
                            headBranch.set(model.getActiveRepository().getHEAD().getName());
                            updateBranchesSideCheckoutButtons();
                            updateBranchButtons();
                        } catch (Exception e) {
                            showExceptionDialog(e);
                        }
                    });
                }
                bodyComponentController.getBranchesButtonsVBox().getChildren().add(branchButton);
                updateBranchesSideCheckoutButtons();
            }
        }
    }

    private Boolean checkIfRTB(String branchName) {
        HashSet<Branch> branchesSet = model.getActiveRepository().getBranches();
        for(Branch branch: branchesSet) {
            if(branch.getName().equals(branchName) && branch.getCollaborationSource().equals(CollaborationSource.REMOTETRACKING)) {
                return true;
            }
        }
        return false;
    }

    public void createLocalRTB(Branch remoteBranch, Boolean shouldCheckout) {
        try {
            model.createRemoteTrackingBranchFromRB(remoteBranch, shouldCheckout);
            bodyComponentController.expandAccordionTitledPane("repository");
            bodyComponentController.displayCommitFilesTree(model.getActiveRepository().getHEAD().getCommit());
            bodyComponentController.setTextAreaString("Local Remote Tracking Branch "
                    + remoteBranch.getName()
                    + " created succesfully \nCurrently on branch "
                    + model.getActiveRepository().getHEAD().getName());
            bodyComponentController.selectTabInBottomTabPane("log");
            updateRepositoryUIAndDetails();
            updateBranchesButtons();
        }catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    public void updateConflictSolverContent(MergeConflict conflict) {
        conflictSolverDialogController.setConflict(conflict);
        conflictSolverDialogController.getAncestorTextArea().setText(
                conflict.getAncestorContent() != null ? conflict.getAncestorContent()
                : "File doesnt exist on Ancestor - \nNewly created file");
        conflictSolverDialogController.getTheirsTextArea().setEditable(false);

        conflictSolverDialogController.getOursTextArea().setText(
                conflict.getOursContent() != null ? conflict.getOursContent()
                        : conflict.getAncestorContent() != null ? "File deleted on Head Branch"
                        : "File doesnt exist on Head Branch" );
        conflictSolverDialogController.getOursTextArea().setEditable(false);

        conflictSolverDialogController.getTheirsTextArea().setText(
                conflict.getTheirsContent() != null ? conflict.getTheirsContent()
                        : conflict.getAncestorContent() != null ? "File deleted on Theirs Branch"
                        : "File doesnt exist on Theirs Branch");
        conflictSolverDialogController.getTheirsTextArea().setEditable(false);
    }

    public ArrayList<LinkedList<MergeConflict>> updateConflictsList(List<MergeConflict> conflicts, List<MergeConflict> solvedConflicts) {
        ListView<MergeConflict> conflictsChooser = conflictsDialogController.getConflictsListView();
        ArrayList<LinkedList<MergeConflict>> list = new ArrayList<LinkedList<MergeConflict>>();
        conflictsChooser.getItems().clear();
        list.add(conflicts.stream()
                .filter(conflict -> !conflict.isSolved())
                .collect(Collectors.toCollection(LinkedList::new)));

        list.add(conflicts.stream()
                .filter(conflict -> conflict.isSolved())
                .collect(Collectors.toCollection(LinkedList::new)));

        for(MergeConflict conflict: solvedConflicts) {
            list.get(1).add(conflict);
        }

        conflictsChooser.getItems().addAll(conflicts);
     //   conflictsChooser.setSelectionModel(SelectionMode.SINGLE);
        conflictsChooser.setCellFactory(conflict -> new ListCell<MergeConflict>(){
            @Override
            protected void updateItem(MergeConflict item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setText(null);
                } else {
                    String conflictName = item.getAncestorComponent() != null ? item.getAncestorComponent().getName()
                                          : item.getOursComponent() != null ? item.getOursComponent().getName()
                                            : item.getTheirsComponent() != null ? item.getTheirsComponent().getName()
                                               : null;
                    setText(conflictName);
                }
            }
        });

        return list;
    }

    private void updateBranchButtons() {
        if(isRepositoryLoaded.get()) {
            Repository activeRepository = model.getActiveRepository();
            HashSet<Branch> branches = activeRepository.getBranches();
            // update branchList on Header Merge as well

            MenuButton toolBarMergeWith = headerComponentController.getToolbarMergeWithButton();
            toolBarMergeWith.getItems().clear();

            ChoiceBox mergeBranchChooser = mergeDialogController.getBranchesChoiceBox();
            mergeBranchChooser.getItems().clear();

            ChoiceBox deleteBranchChooser = deleteBranchDialogController.getBranchesChoiceBox();
            deleteBranchChooser.getItems().clear();

            ChoiceBox checkOutBranchChooser = checkOutDialogController.getBranchesChoiceBox();
            checkOutBranchChooser.getItems().clear();

            for(Branch branch: branches) {
                if(!branch.equals(model.getActiveRepository().getHEAD())) { // for each branch except the HEAD branch
                    String branchName = branch.getName();
                    mergeBranchChooser.getItems().add(branch);
                    deleteBranchChooser.getItems().add(branchName);
                    checkOutBranchChooser.getItems().add(branchName);

                    MenuItem branchMenuItem = new MenuItem((branch.getCollaborationSource().equals(CollaborationSource.REMOTE) ?
                            activeRepository.getName() + "/" : "") +
                            branchName);

                    branchMenuItem.setOnAction(event -> {
                        try {
                            merge(branch);
                        } catch (Exception e) {
                            showExceptionDialog(e);
                        }
                    });
                    toolBarMergeWith.getItems().add(branchMenuItem);
                }
            }

            mergeBranchChooser.setConverter(new StringConverter<Branch>() {
                @Override
                public String toString(Branch branch) {
                    if(branch.getCollaborationSource().equals(CollaborationSource.REMOTE)) {
                        return activeRepository.getName() + "/" + branch.getName();
                    }
                    return branch.getName();
                }

                @Override
                public Branch fromString(String string) {
                    return null;
                }
            });

            if(isRemoteRepositoryExists.get()) {
                mergeBranchChooser = createRTBDialogController.getBranchesChoiceBox();
                mergeBranchChooser.getItems().clear();

                for(Branch branch: branches) {
                    if(branch.getCollaborationSource().equals(CollaborationSource.REMOTE) && !branch.equals(model.getActiveRepository().getHEAD())) {
                        String branchName = branch.getName();
                        mergeBranchChooser.getItems().add(branch);
                    }
                }

                mergeBranchChooser.setConverter(new StringConverter<Branch>() {
                    @Override
                    public String toString(Branch branch) {
                        return branch.getName();
                    }

                    @Override
                    public Branch fromString(String string) { return null; }
                });
            }
        }
    }

    private void updateBranchesSideCheckoutButtons() {
        String HEADname = model.getActiveRepository().getHEAD().getName();
        for (Node node : bodyComponentController.getBranchesButtonsVBox().getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setDisable(button.getText().equals(HEADname));
            }
        }
    }

    public void checkForUncommitedChanges() {
        if (isRepositoryLoaded.get()) {
            try {
                this.isUncommitedChanges.set(!model.showStatus().isEmptyLog());
            } catch (IOException e) {
                e.printStackTrace();
            };
        }
    }

    public boolean yesNoCancelDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes) {
            return true;
        } else {
            return false;
        }
    }

    public void setDefaultTheme() {
        view.getScene().getStylesheets().clear();
    }

    public void setDraculaTheme() {
        view.getScene().getStylesheets().clear();
        view.getScene().getStylesheets().add("Themes/Dracula.css");
    }

    public void setJungleGreenTheme() {
        view.getScene().getStylesheets().clear();
        view.getScene().getStylesheets().add("/Themes/JungleGreen.css");
    }

    @FXML
    public void showExceptionDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    @FXML
    public void showExceptionStackTraceDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Look, an Exception Dialog");
        alert.setContentText(ex.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea;
        textArea = new TextArea(exceptionText);
        textArea.setEditable(false);

//        GridPane.setVgrow(textArea, Priority.ALWAYS);
//        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
//        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

}
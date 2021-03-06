package header;

import Engine.Branch;
import app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HeaderController {

    // FXML elements
    @FXML private MenuItem        createNewRepositoryMenuBarButton;
    @FXML private MenuItem        importFromXMLMenuBarButton;
    @FXML private MenuItem        loadrepositoryFromPathMenuBarButton;
    @FXML private MenuItem        cloneMenuBarButton;
    @FXML private MenuItem        fetchMenuBarButton;
    @FXML private MenuItem        pullMenuBarButton;
    @FXML private MenuItem        pushMenuBarButton;
    @FXML private MenuItem        changeActiveUserTopMenuItem;
    @FXML private MenuItem        commitMenuBarButton;
    @FXML private MenuItem        showStatusMenuBarButton;
    @FXML private MenuItem        createNewBranchMenuBarButton;
    @FXML private MenuItem        deleteBranchMenuBarButton;
    @FXML private MenuItem        checkoutMenuBarButton;
    @FXML private MenuItem        mergeWithMenuBarButton;
    @FXML private SplitMenuButton commitSplitMenuButton;
    @FXML private MenuItem        showStatusSplitMenuButton;
    @FXML private MenuButton      toolbarMergeWithButton;
    @FXML private Button          toolbarPullButton;
    @FXML private Button          toolbarPushButton;
    @FXML private Button          toolbarFetchButton;
    @FXML private MenuButton      activeUserMenuButton;
    @FXML private MenuItem        changeActiveUserSideMenuItem;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void initialize() {
    }

    public void bindProperties() {
        SimpleBooleanProperty isRepositoryLoaded = appController.getIsRepositoryLoaded();
        SimpleBooleanProperty isRemoteRepositoryExists = appController.getIsRemoteRepositoryExists();
        SimpleStringProperty activeUser = appController.getActiveUser();

        activeUserMenuButton.textProperty().bind(activeUser);
        fetchMenuBarButton.disableProperty().bind(isRemoteRepositoryExists.not());
        pullMenuBarButton.disableProperty().bind(isRemoteRepositoryExists.not());
        pushMenuBarButton.disableProperty().bind(isRemoteRepositoryExists.not());

        commitMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        showStatusMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        createNewBranchMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        deleteBranchMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        checkoutMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        mergeWithMenuBarButton.disableProperty().bind(isRepositoryLoaded.not());
        commitSplitMenuButton.disableProperty().bind(isRepositoryLoaded.not());
        showStatusSplitMenuButton.disableProperty().bind(isRepositoryLoaded.not());
        toolbarMergeWithButton.disableProperty().bind(isRepositoryLoaded.not());
        toolbarPullButton.disableProperty().bind(isRemoteRepositoryExists.not());
        toolbarPushButton.disableProperty().bind(isRemoteRepositoryExists.not());
        toolbarFetchButton.disableProperty().bind(isRemoteRepositoryExists.not());
    }

    public void menuItemsEventHandler(ActionEvent event) {
        Object source = event.getSource();
        String id;

        appController.checkForUncommitedChanges();

        if (source instanceof MenuItem) {
            id = ((MenuItem) source).getId();
        } else if (source instanceof MenuButton) {
            id = ((MenuButton) source).getId();
        } else if (source instanceof SplitMenuButton) {
            id = ((SplitMenuButton) source).getId();
        } else if (source instanceof Button) {
            id = ((Button) source).getId();
        } else {
            id = "";
        }

        switch(id)
        {
            case "createNewRepositoryMenuBarButton":
                appController.createNewRepository();
                break;
            case "importFromXMLMenuBarButton":
                appController.importRepositoryFromXML();
                break;
            case "loadrepositoryFromPathMenuBarButton":
                appController.switchRepository();
                break;
            case "cloneMenuBarButton":
                appController.cloneDialog();
                break;
            case "fetchMenuBarButton":
                appController.fetch();
                break;
            case "pullMenuBarButton":
                appController.pull();
                break;
            case "pushMenuBarButton":
                appController.push();
                break;
            case "changeActiveUserTopMenuItem":
            case "changeActiveUserSideMenuItem":
                appController.switchActiveUser();
                break;
            case "commitMenuBarButton":
            case "commitSplitMenuButton":
                appController.commit();
                break;
            case "showStatusMenuBarButton":
            case "showStatusSplitMenuButton":
                appController.showStatus();
                break;
            case "createNewBranchMenuBarButton":
                appController.createNewBranchDialog();
                break;
            case "deleteBranchMenuBarButton":
                 appController.deleteBranchDialog();
                break;
            case "checkoutMenuBarButton":
                appController.checkoutDialog();
                break;
            case "mergeWithMenuBarButton":
                appController.mergeDialog();
                break;
            case "toolbarMergeWithButton":
                appController.mergeDialog();
                break;
            case "toolbarPullButton":
                appController.pull();
                break;
            case "toolbarPushButton":
                appController.push();
                break;
            case "toolbarFetchButton":
                appController.fetch();
                break;
            case "setDefaultTheme":
                appController.setDefaultTheme();
                break;
            case "setDraculaTheme":
                appController.setDraculaTheme();
                break;
            case "setJungleGreenTheme":
                appController.setJungleGreenTheme();
                break;

            default:
                System.out.println("no match");
        }
    }

    public MenuButton getToolbarMergeWithButton() { return toolbarMergeWithButton;
    }
}
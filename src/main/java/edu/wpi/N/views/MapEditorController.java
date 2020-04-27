package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.UINode;
import java.io.IOException;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditorController implements Controller {
  private App mainApp;

  @FXML private MapBaseController mapBase;

  @FXML Pane pn_display;
  @FXML Pane pn_editor;
  @FXML Button btn_home;

  final int DEFAULT_FLOOR = 4;
  final String DEFAULT_BUILDING = "Faulkner";
  /*
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_LINE_COLOR = Color.BLACK;
  final double DEFAULT_LINE_WIDTH = 4;
  final Color ADD_NODE_COLOR = Color.BLACK;
  final Color DELETE_NODE_COLOR = Color.RED;
  final Color EDIT_NODE_COLOR = Color.RED;
  final double DEFAULT_CIRCLE_OPACITY = 0.7;
  final double DEFAULT_CIRCLE_RADIUS = 7;
   */

  final double SCREEN_WIDTH = 1920;
  final double SCREEN_HEIGHT = 1080;
  final double IMAGE_WIDTH = 2475;
  final double IMAGE_HEIGHT = 1485;
  final double MAP_WIDTH = 1661;
  final double MAP_HEIGHT = 997;
  final double HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
  final double VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;
  Mode mode;

  private enum Mode {
    NO_STATE,
    ADD_NODE,
    DELETE_NODE,
    EDIT_NODE,
    ADD_EDGE,
    DELETE_EDGE,
    EDIT_EDGE
  }

  // HashBiMap<Circle, UINode> nodesMap;
  // HashBiMap<Line, UIEdge> edgesMap;
  MapEditorAddNodeController controllerAddNode;
  MapEditorDeleteNodeController controllerDeleteNode;
  MapEditorEditNodeController controllerEditNode;

  int currentFloor;
  String currentBuilding;
  // Add Node Variable
  UINode tempUINode;
  // Delete Node Variable
  LinkedList<UINode> deleteNodeCircles;
  // Edit Node Variable
  UINode editNodeCircle;
  UINode lastEditNodeCircle;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    currentFloor = DEFAULT_FLOOR;
    currentBuilding = DEFAULT_BUILDING;
    // nodesMap = HashBiMap.create();
    // edgesMap = HashBiMap.create();
    mode = Mode.NO_STATE;
    loadFloor();
    tempUINode = null;
    deleteNodeCircles = new LinkedList<>();
    editNodeCircle = null;
    lastEditNodeCircle = null;
  }

  private void loadFloor() throws DBException {
    // LinkedList<DbNode> floorNodes = MapDB.floorNodes(currentFloor, currentBuilding);
    // LinkedList<DbNode[]> floorEdges = MapDB.getFloorEdges(currentFloor);
    // HashMap<String, UINode> conversion = createUINodes(floorNodes, DEFAULT_CIRCLE_COLOR);
    // createUIEdges(conversion, floorEdges, DEFAULT_LINE_COLOR);
    mapBase.populateMap();
  }

  /*
  private HashMap<String, UINode> createUINodes(LinkedList<DbNode> nodes, Color c) {
    HashMap<String, UINode> conversion = new HashMap<>();
    for (DbNode DBnode : nodes) {
       UINode node = createCircle(scaleX(DBnode.getX()), scaleY(DBnode.getY()), c);
      UINode UInode = new UINode(circle, DBnode);
      nodesMap.put(circle, UInode);
      conversion.put(DBnode.getNodeID(), UInode);
    }
    return conversion;
  }
   */

  /*
  private void createUIEdges(
      HashMap<String, UINode> conversion, LinkedList<DbNode[]> edges, Color c) {
    for (DbNode[] edge : edges) {
      Line line =
          createLine(
              scaleX(edge[0].getX()),
              scaleY(edge[0].getY()),
              scaleX(edge[1].getX()),
              scaleY(edge[1].getY()),
              c);
      UIEdge UIedge = new UIEdge(line, edge);
      conversion.get(edge[0].getNodeID()).addEdge(UIedge);
      conversion.get(edge[1].getNodeID()).addEdge(UIedge);
      edgesMap.put(line, UIedge);
    }
  }
   */

  /*
  private Circle createCircle(double x, double y, Color c) {
     UINode node = new Circle();
    circle.setRadius(DEFAULT_CIRCLE_RADIUS);
    circle.setCenterX(x);
    circle.setCenterY(y);
    circle.setFill(c);
    circle.setOpacity(DEFAULT_CIRCLE_OPACITY);
    circle.setOnMouseDragged(event -> this.handleCircleDragEvents(event, circle));
    circle.setOnMouseClicked(event -> this.handleCircleClickedEvents(event, circle));
    pn_display.getChildren().add(circle);
    return circle;
  }
   */

  /*
  private Line createLine(double x1, double y1, double x2, double y2, Color c) {
    Line line = new Line(x1, y1, x2, y2);
    line.setStroke(c);
    line.setStrokeWidth(DEFAULT_LINE_WIDTH);
    pn_display.getChildren().add(line);
    return line;
  }
   */

  private double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  private double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }

  private void changeEditor() throws IOException {
    resetAll();
    if (mode == Mode.ADD_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorAddNode.fxml");
    } else if (mode == Mode.DELETE_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorDeleteNode.fxml");
    } else if (mode == Mode.EDIT_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorEditNode.fxml");
    }
  }

  private void loadEditor(String path) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
    Pane pane = loader.load();
    if (mode == Mode.ADD_NODE) {
      controllerAddNode = loader.getController();
    } else if (mode == Mode.DELETE_NODE) {
      controllerDeleteNode = loader.getController();
    } else if (mode == Mode.EDIT_NODE) {
      controllerEditNode = loader.getController();
    }
    pn_editor.getChildren().add(pane);
    pn_editor.setVisible(true);
  }

  private void handleNodeDragEvents(MouseEvent event, UINode node) {
    if (mode == Mode.ADD_NODE && node == tempUINode) {
      onCircleAddNodeDragged(event, node);
    }
    if (mode == Mode.EDIT_NODE) {
      onBtnCancelEditNodeClicked();
      onBtnConfirmEditNodeClicked();
      onTxtPosEditNodeTextChanged(node);
      onCircleEditNodeDragged(event, node);
    }
  }

  private void handleNodeClickedEvents(MouseEvent event, UINode node) {
    if (mode == Mode.DELETE_NODE) {
      onCircleDeleteNodeClicked(event, node);
    }
    if (mode == Mode.EDIT_NODE) {
      onBtnCancelEditNodeClicked();
      onBtnConfirmEditNodeClicked();
      onTxtPosEditNodeTextChanged(node);
      onCircleEditNodeClicked(event, node);
    }
  }

  private void onCircleEditNodeClicked(MouseEvent event, UINode uiNode) {
    if (editNodeCircle != null && editNodeCircle != uiNode) {
      // DbNode node = nodesMap.get(editNodeCircle).getDBNode();
      // editNodeCircle.setCenterX(scaleX(node.getX()));
      // editNodeCircle.setCenterY(scaleY(node.getY()));
      // editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
      cancelEditNode();
    }
    editNodeCircle = uiNode;
    // Set color of node to "Edit"
    DbNode dbNode = mapBase.getDbFromUi(editNodeCircle);
    controllerEditNode.setShortName(dbNode.getShortName());
    controllerEditNode.setLongName(dbNode.getLongName());
    controllerEditNode.setPos(event.getX(), event.getY());
  }

  private void onCircleEditNodeDragged(MouseEvent event, UINode uiNode) {
    if (editNodeCircle != null && editNodeCircle != uiNode) {
      // DbNode node = nodesMap.get(editNodeCircle).getDBNode();
      // editNodeCircle.setCenterX(scaleX(node.getX()));
      // editNodeCircle.setCenterY(scaleY(node.getY()));
      // editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
      cancelEditNode();
    }
    editNodeCircle = uiNode;
    // Set color of node to "Edit"
    controllerEditNode.setPos(event.getX(), event.getY());
    uiNode.setPos(event.getX(), event.getY()); // Also moves attached edges
    DbNode dbNode = mapBase.getDbFromUi(editNodeCircle);
    controllerEditNode.setShortName(dbNode.getShortName());
    controllerEditNode.setLongName(dbNode.getLongName());

    /*
    UINode uiNode = nodesMap.get(circle);
    for (UIEdge edges : uiNode.getEdges()) {
      DbNode firstNode = edges.getDBNodes()[0];
      DbNode secondNode = edges.getDBNodes()[1];
      if (firstNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setStartX(event.getX());
        edges.getLine().setStartY(event.getY());
      } else if (secondNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setEndX(event.getX());
        edges.getLine().setEndY(event.getY());
      }
    }
     */
  }

  private void cancelEditNode() {
    DbNode dbNode = mapBase.getDbFromUi(editNodeCircle);
    editNodeCircle.setPos(dbNode.getX(), dbNode.getY());
    /*
    DbNode node = nodesMap.get(editNodeCircle).getDBNode();
    UINode uiNode = nodesMap.get(editNodeCircle);
    for (UIEdge edges : uiNode.getEdges()) {
      DbNode firstNode = edges.getDBNodes()[0];
      DbNode secondNode = edges.getDBNodes()[1];
      if (firstNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setStartX(scaleX(uiNode.getDBNode().getX()));
        edges.getLine().setStartY(scaleY(uiNode.getDBNode().getY()));
      } else if (secondNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setEndX(scaleX(uiNode.getDBNode().getX()));
        edges.getLine().setEndY(scaleY(uiNode.getDBNode().getY()));
      }
    }
     */
  }

  private void onCircleDeleteNodeClicked(MouseEvent event, UINode uiNode) {
    // uiNode set delete color
    /*
    if (circle.getFill() == DEFAULT_CIRCLE_COLOR) {
      circle.setFill(DELETE_NODE_COLOR);
      deleteNodeCircles.add(circle);
      controllerDeleteNode.addLstDeleteNode(nodesMap.get(circle).getDBNode().getShortName());
    } else if (circle.getFill() == DELETE_NODE_COLOR) {
      circle.setFill(DEFAULT_CIRCLE_COLOR);
      deleteNodeCircles.remove(circle);
      controllerDeleteNode.removeLstDeleteNode(nodesMap.get(circle).getDBNode().getShortName());
    }
     */
  }

  private void handleDeleteNodeRightClick() throws IOException, DBException {
    mode = Mode.DELETE_NODE;
    changeEditor();
    onBtnCancelDeleteNodeClicked();
    onBtnConfirmDeleteNodeClicked();
  }

  private void handleEditNodeRightClick() throws IOException {
    mode = Mode.EDIT_NODE;
    changeEditor();
  }

  // Pane Display Clicked
  public void onPaneDisplayClicked(MouseEvent event) throws IOException {
    // Add Node
    if (event.getClickCount() == 2 && mode != Mode.ADD_NODE) {
      onPaneDisplayClickedAddNode(event);
    }
    if (event.getButton() == MouseButton.SECONDARY) {
      ContextMenu menu = new ContextMenu();
      MenuItem deleteNode = new MenuItem("Delete Node");
      MenuItem editNode = new MenuItem("Edit Node");
      deleteNode.setOnAction(
          e -> {
            try {
              handleDeleteNodeRightClick();
            } catch (IOException | DBException ex) {
              ex.printStackTrace();
            }
          });
      editNode.setOnAction(
          e -> {
            try {
              handleEditNodeRightClick();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          });
      menu.getItems().addAll(deleteNode, editNode);
      menu.show(mainApp.getStage(), event.getSceneX(), event.getSceneY());
    }
  }

  private void onPaneDisplayClickedAddNode(MouseEvent event) throws IOException {
    mode = Mode.ADD_NODE;
    changeEditor();
    // Add node
    /*
    tempUINode = createCircle(event.getX(), event.getY(), ADD_NODE_COLOR);
    controllerAddNode.setPos(event.getX(), event.getY());
    onTxtPosAddNodeTextChanged(tempUINode);
    onBtnConfirmAddNodeClicked(tempUINode);
     */
    onBtnCancelAddNodeClicked();
  }

  public void onPaneDisplayKeyPressed(KeyEvent event) {
    // Add Node
    if (mode == Mode.ADD_NODE) {
      if (event.getCode() == KeyCode.ENTER) {
        System.out.println("Enter Pressed");
      }
    }
  }

  public void onCircleAddNodeDragged(MouseEvent event, UINode uiNode) {
    uiNode.setPos(event.getX(), event.getY());
    controllerAddNode.setPos(event.getX(), event.getY());
  }

  // Add Node FXML
  public void onTxtPosAddNodeTextChanged(UINode uiNode) {
    controllerAddNode
        .getTxtXPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleXPosAddNode(uiNode, newValue)));
    controllerAddNode
        .getTxtYPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleYPosAddNode(uiNode, newValue)));
  }

  public void onTxtPosEditNodeTextChanged(UINode uiNode) {
    controllerEditNode
        .getTxtXPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleXPosEditNode(uiNode, newValue)));
    controllerEditNode
        .getTxtYPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleYPosEditNode(uiNode, newValue)));
  }

  private void setCircleXPosAddNode(UINode uiNode, String newValue) {
    try {
      uiNode.setPos(Double.parseDouble(newValue), uiNode.getY());
      // circle.setCenterX(Double.parseDouble(newValue));

    } catch (NumberFormatException e) {
      controllerAddNode.setPos(uiNode.getX(), uiNode.getY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void setCircleYPosAddNode(UINode uiNode, String newValue) {
    try {
      uiNode.setPos(Double.parseDouble(newValue), uiNode.getY());
      // circle.setCenterY(Double.parseDouble(newValue));
    } catch (NumberFormatException e) {
      controllerAddNode.setPos(uiNode.getX(), uiNode.getY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void setCircleXPosEditNode(UINode uiNode, String newValue) {
    try {
      if (editNodeCircle == uiNode || (mode != Mode.EDIT_NODE)) {
        uiNode.setPos(Double.parseDouble(newValue), uiNode.getY());

        /*
        circle.setCenterX(Double.parseDouble(newValue));
        DbNode node = nodesMap.get(circle).getDBNode();
        UINode uiNode = nodesMap.get(circle);
        for (UIEdge edges : uiNode.getEdges()) {
          DbNode firstNode = edges.getDBNodes()[0];
          DbNode secondNode = edges.getDBNodes()[1];
          if (firstNode.getNodeID().equals(Double.parseDouble(newValue))) {
            edges.getLine().setStartX(Double.parseDouble(newValue));
          } else if (secondNode.getNodeID().equals(node.getNodeID())) {
            edges.getLine().setEndX(Double.parseDouble(newValue));
          }
        }
         */
      }

    } catch (NumberFormatException e) {
      controllerEditNode.setPos(uiNode.getX(), uiNode.getY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void setCircleYPosEditNode(UINode uiNode, String newValue) {
    try {
      if (editNodeCircle == uiNode || (mode != Mode.EDIT_NODE)) {
        uiNode.setPos(uiNode.getX(), Double.parseDouble(newValue));
        /*
        circle.setCenterX(Double.parseDouble(newValue));
        DbNode node = nodesMap.get(circle).getDBNode();
        UINode uiNode = nodesMap.get(circle);
        for (UIEdge edges : uiNode.getEdges()) {
          DbNode firstNode = edges.getDBNodes()[0];
          DbNode secondNode = edges.getDBNodes()[1];
          if (firstNode.getNodeID().equals(Double.parseDouble(newValue))) {
            edges.getLine().setStartY(Double.parseDouble(newValue));
          } else if (secondNode.getNodeID().equals(node.getNodeID())) {
            edges.getLine().setEndY(Double.parseDouble(newValue));
          }
        }

         */
      }

    } catch (NumberFormatException e) {
      controllerEditNode.setPos(uiNode.getX(), uiNode.getY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void onBtnConfirmAddNodeClicked(UINode node) {
    controllerAddNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              try {
                mode = Mode.NO_STATE;
                String strX = controllerAddNode.getXPos();
                String strY = controllerAddNode.getYPos();
                int x = 0;
                int y = 0;
                try {
                  x = (int) scaleXDB(Double.parseDouble(strX));
                  y = (int) scaleYDB(Double.parseDouble(strY));
                } catch (NumberFormatException e) {
                  displayErrorMessage("Invalid input");
                  return;
                }
                String type = controllerAddNode.getType();
                String longName = controllerAddNode.getShortName();
                String shortName = controllerAddNode.getLongName();
                if (type == null || longName == null || shortName == null) {
                  displayErrorMessage("Invalid input");
                  return;
                }

                // TODO: Add nodes

                DbNode newNode =
                    MapDB.addNode(x, y, currentFloor, currentBuilding, type, longName, shortName);
                /*
                LinkedList list = new LinkedList();
                list.add(newNode);
                createUINodes(list, DEFAULT_CIRCLE_COLOR);
                pn_display.getChildren().remove(circle);

                pn_editor.setVisible(false);

                 */
              } catch (DBException e) {
                e.printStackTrace();
              }
            });
  }

  private void onBtnConfirmDeleteNodeClicked() throws DBException {
    controllerDeleteNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              for (UINode uiNode : deleteNodeCircles) {
                /*
                UINode node = nodesMap.get(circle);
                try {
                  MapDB.deleteNode(mapBase.getDbFromUi(uiNode).getNodeID());
                } catch (DBException e) {
                  e.printStackTrace();
                }

                for (UIEdge edge : node.getEdges()) {
                  DbNode[] edgeNodes = edge.getDBNodes();
                  try {
                    MapDB.removeEdge(edgeNodes[0].getNodeID(), edgeNodes[1].getNodeID());
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                  edgesMap.remove(edge.getLine());
                  pn_display.getChildren().remove(edge.getLine());
                }
                pn_display.getChildren().remove(circle);
                nodesMap.remove(circle);
                 */
              }
              resetDeleteNode();
              pn_editor.setVisible(false);
            });
  }

  private void onBtnConfirmEditNodeClicked() {
    controllerEditNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              String strX = controllerEditNode.getXPos();
              String strY = controllerEditNode.getYPos();
              int x = 0;
              int y = 0;
              try {
                x = (int) scaleXDB(Double.parseDouble(strX));
                y = (int) scaleYDB(Double.parseDouble(strY));
              } catch (NumberFormatException e) {
                displayErrorMessage("Invalid input");
                return;
              }
              String longName = controllerEditNode.getShortName();
              String shortName = controllerEditNode.getLongName();
              if (longName == null || shortName == null) {
                displayErrorMessage("Invalid input");
                return;
              }
              String id = mapBase.getDbFromUi(editNodeCircle).getNodeID();
              // String id = nodesMap.get(editNodeCircle).getDBNode().getNodeID();
              try {
                MapDB.modifyNode(id, x, y, longName, shortName);
              } catch (DBException e) {
                e.printStackTrace();
              }
              pn_editor.setVisible(false);
              // TODO: Set node highlight
              // editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
              editNodeCircle = null;
            });
  }

  private void onBtnCancelAddNodeClicked() {
    controllerAddNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              pn_editor.setVisible(false);
              pn_display.getChildren().remove(tempUINode);
            });
  }

  private void onBtnCancelDeleteNodeClicked() {
    controllerDeleteNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              for (UINode node : deleteNodeCircles) {
                node.setSelected(false);
                // circle.setFill(DEFAULT_CIRCLE_COLOR);
              }
              deleteNodeCircles.clear();
              pn_editor.setVisible(false);
            });
  }

  private void onBtnCancelEditNodeClicked() {
    controllerEditNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              // editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
              editNodeCircle.setSelected(false);
              // editNodeCircle.setCenterX(scaleX(nodesMap.get(editNodeCircle).getDBNode().getX()));
              // editNodeCircle.setCenterY(scaleY(nodesMap.get(editNodeCircle).getDBNode().getY()));
              DbNode dbNode = mapBase.getDbFromUi(editNodeCircle);
              editNodeCircle.setPos(dbNode.getX(), dbNode.getY());
              cancelEditNode();
              editNodeCircle = null;
              pn_editor.setVisible(false);
            });
  }

  private void resetAll() {
    pn_editor.setVisible(false);
    resetAddNode();
    resetDeleteNode();
    resetEditNode();
  }

  private void resetAddNode() {
    if (tempUINode != null) {
      pn_display.getChildren().remove(tempUINode);
    }
  }

  private void resetDeleteNode() {
    for (UINode node : deleteNodeCircles) {
      // circle.setFill(DEFAULT_CIRCLE_COLOR);
      node.setSelected(false);
    }
    deleteNodeCircles.clear();
  }

  private void resetEditNode() {
    if (editNodeCircle != null) {
      // editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
      editNodeCircle.setSelected(false);
      // editNodeCircle.setCenterX(scaleX(nodesMap.get(editNodeCircle).getDBNode().getX()));
      // editNodeCircle.setCenterY(scaleY(nodesMap.get(editNodeCircle).getDBNode().getY()));
      DbNode dbNode = mapBase.getDbFromUi(editNodeCircle);
      editNodeCircle.setPos(dbNode.getX(), dbNode.getY());
      cancelEditNode();
    }
    editNodeCircle = null;
  }

  private double scaleXDB(double x) {
    return x / HORIZONTAL_SCALE;
  }

  private double scaleYDB(double y) {
    return y / VERTICAL_SCALE;
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  public void onBtnHomeClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }
}

package com.cgvsu;

import com.cgvsu.deletevertex.DeleteVertex;
import com.cgvsu.objreader.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

import static com.cgvsu.ExceptionDialog.throwExceptionWindow;

public class GuiController {


    private float x = 0;
    final private float TRANSLATION = 0.5F;

    private List<Model> models = new ArrayList<>();

    private Model selectedModel;

    private List<Float> modelCenters = new ArrayList<>();

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;
    @FXML
    private VBox modelsMenuBox;

    private List<Camera> cameras = new ArrayList<>();

    private int currentCameraNum = 1;


    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private ToggleGroup camerasPinGroup = new ToggleGroup();
    private ToggleGroup camerasGroup = new ToggleGroup();

    @FXML
    private Button deleteCameraButton;
    @FXML
    private VBox camerasMenuBox;
    private int firstCameraIndex = 0;


    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (models != null) {
                for (Model model: models){
                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, model, (int) width, (int) height);
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            addToModelsList(fileName);
            Model newModel = ObjReader.read(fileContent);
            if (newModel == null) {
                throwExceptionWindow(ExceptionDialog.Operation.READING);
            }
            selectedModel = newModel;
            models.add(selectedModel);

            translateModel(newModel, x, 0, 0);
            if (models.isEmpty()) {
                newModel.selected = true;
                selectedModel = newModel;
            }
            models.add(newModel);
            modelCenters.add(x);
            x += newModel.xSize;
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }


    public void saveModel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj");
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());

        if (file != null) {
            ObjWriter.write(selectedModel, file.getAbsolutePath());
        }
    }

    public void deleteVerticeses(ActionEvent actionEvent){
        // Создаем диалог для ввода списка вершин
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удалить вершины");
        dialog.setHeaderText("Введите ID вершин для удаления (через запятую):");
        dialog.setContentText("Вершины:");

        // Отображаем диалог и ждем ответа
        String result = dialog.showAndWait().orElse("");

        if (!result.isEmpty()) {
            // Разделяем введенные данные на список вершин и удаляем лишние пробелы
            String[] verticesArray = result.split(",");
            List<Integer> verticesToDelete = new ArrayList<>();

            // Преобразуем строки в целые числа и добавляем в список
            for (String vertexStr : verticesArray) {
                try {
                    verticesToDelete.add(Integer.parseInt(vertexStr.trim())); // парсим строку в Integer
                } catch (NumberFormatException e) {
                    showError("Ошибка ввода", "Некоторые элементы не являются целыми числами.");
                    return; // Выход из метода, если был неправильный ввод
                }
            }
            boolean flag1 = askForFlag("Удалять нормали?");
            boolean flag2 = askForFlag("Удалять текстурные вершины?");
            // Создаем экземпляр DeleteVertex для удаления вершин

            DeleteVertex.deleteVertex(selectedModel,verticesToDelete,flag1,flag2) ;
            // Удаляем вершины
        } else {
            showError("Ошибка", "Вы не ввели ни одной вершины.");
        }
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean askForFlag(String headerText) {
        // Создаем диалог для ввода флажка (true/false)
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Да", "Да", "Нет");
        dialog.setTitle("Выбор флажка");
        dialog.setHeaderText(headerText);
        dialog.setContentText("Выберите флажок:");

        // Отображаем диалог и ждем ответа
        String result = dialog.showAndWait().orElse("false");

        return result.equals("true");
    }

    @FXML
    private void addToModelsList(Path fileName) {
        String name = fileName.toString();
        MenuButton modelButton = new MenuButton(name.split("\\\\")[name.split("\\\\").length - 1]);
        modelButton.setMinWidth(240);
        modelButton.setMaxWidth(240);
        CheckMenuItem polygonMeshShow = new CheckMenuItem("Полигональная сетка");
        polygonMeshShow.setSelected(true);
        CheckMenuItem textureShow = new CheckMenuItem("Текстура");
        textureShow.setSelected(false);
        CheckMenuItem lightingShow = new CheckMenuItem("Освещение");
        lightingShow.setSelected(false);
        RadioMenuItem pinCamera = new RadioMenuItem("Центрировать камеру");
        pinCamera.setSelected(models.isEmpty());
        int modelIndex = models.size();

        pinCamera.setOnAction(actionEvent -> setCameraTarget(modelIndex));

        camerasPinGroup.getToggles().add(camerasPinGroup.getToggles().size(), pinCamera);
        modelButton.getItems().add(pinCamera);

        modelButton.getItems().add(polygonMeshShow);
        modelButton.getItems().add(textureShow);
        modelButton.getItems().add(lightingShow);

        modelsMenuBox.getChildren().add(modelButton);
    }

    private void setCameraTarget(int ind) {
        selectedModel = models.get(ind);
        for (Model model : models) {
            model.selected = (model == selectedModel);
        }
        camera.setCenteredModel(ind);
        camera.setTarget(new Vector3f(modelCenters.get(ind), 0, 0));
    }

    @FXML
    private void clearAllModels () {
        models.clear();
        x = 0;
        clearModelsList();
    }

    @FXML
    private void clearModelsList() {
        modelsMenuBox.getChildren().clear();
        camerasPinGroup.getToggles().clear();
    }

    private void translateModel(Model model, float x, float y, float z) {
        for (com.cgvsu.math.Vector3f vertex : model.getVertices()) {
            vertex.x += x;
            vertex.y += y;
            vertex.z += z;
        }
    }
    private void chooseCamera(int ind) {
        camera = cameras.get(ind);
        if (!camerasGroup.getToggles().isEmpty()) {
            camerasGroup.selectToggle(camerasGroup.getToggles().get(ind));
        }
        if (!camerasPinGroup.getToggles().isEmpty()) {
            camerasPinGroup.selectToggle(camerasPinGroup.getToggles().get(camera.getCenteredModel()));
        }
    }

    public void addCamera(ActionEvent actionEvent) {
        cameras.add(new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100
        ));



        deleteCameraButton.setDisable(cameras.size() <= 1);
        RadioButton newCameraButton = new RadioButton(String.format("Camera %d", currentCameraNum++));
        newCameraButton.setMinHeight(25);
        newCameraButton.setSelected(true);
        final int cameraIndex = cameras.size() - 1;
        newCameraButton.setOnAction(event -> chooseCamera(cameraIndex));
        camerasGroup.getToggles().add(cameraIndex, newCameraButton);
        camerasMenuBox.getChildren().add(newCameraButton);
        chooseCamera(cameraIndex);
    }

    public void deleteCamera(ActionEvent actionEvent) {
        int cameraIndex = cameras.indexOf(camera);
        camera.isVisible = false;
        camerasMenuBox.getChildren().remove(calcCameraInBoxIndex(cameraIndex) + 2);
        calcFirstCamera();
        chooseCamera(firstCameraIndex);
        if (camerasMenuBox.getChildren().size() == 4) {
            deleteCameraButton.setDisable(true);
        }
    }
    private void calcFirstCamera() {
        firstCameraIndex = 0;
        for (Camera cam : cameras) {
            if (cam.isVisible) {
                break;
            } else {
                firstCameraIndex++;
            }
        }
    }

    private int calcCameraInBoxIndex(int n) {
        int result = 0;
        for (int i = 0; i <= n; i++) {
            if (cameras.get(i).isVisible) {
                result++;
            }
        }
        return result;
    }
}



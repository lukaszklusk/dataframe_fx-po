package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import dataframe.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        final DataFrame[] dtf = new DataFrame[1];

        primaryStage.setTitle("Dataframe");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label filepathL = new Label("Ścieżka do pliku: ");
        grid.add(filepathL,0,0);

        TextField filepath = new TextField();
        grid.add(filepath,1,0);

        Button findFile = new Button("Znajdź plik");
        HBox hbFFile = new HBox(10);
        hbFFile.setAlignment(Pos.BOTTOM_RIGHT);
        hbFFile.getChildren().add(findFile);
        grid.add(hbFFile, 2, 0);

        FileChooser fileChooser = new FileChooser();

        findFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if(file != null) {
                    filepath.setText(file.getAbsolutePath());
                }
            }
        });

        Label dtfWidthL = new Label("Podaj ilość kolumn: ");
        grid.add(dtfWidthL,0,1);

        TextField dtfWidth = new TextField();
        grid.add(dtfWidth,1,1);

        Button btn = new Button("Wczytaj");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 2);


        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(filepath.getText().isEmpty() || dtfWidth.getText().isEmpty()){
                    GridPane errorGrid = new GridPane();
                    errorGrid.setAlignment(Pos.CENTER);
                    errorGrid.setHgap(10);
                    errorGrid.setVgap(10);
                    errorGrid.setPadding(new Insets(25, 25, 25, 25));
                    Label errorMsg = new Label("Proszę uzupełnić pola.");
                    errorGrid.add(errorMsg, 0, 0);
                    Scene errorScene = new Scene(errorGrid, 350, 100);

                    Button errorBtn = new Button("OK");
                    HBox hbErrBtn = new HBox(10);
                    hbErrBtn.setAlignment(Pos.CENTER);
                    hbErrBtn.getChildren().add(errorBtn);
                    errorGrid.add(hbErrBtn, 0, 1);

                    Stage errorWindow = new Stage();
                    errorWindow.setTitle("Błąd!");
                    errorWindow.setScene(errorScene);
                    errorWindow.initModality(Modality.WINDOW_MODAL);

                    errorWindow.initOwner(primaryStage);

                    errorWindow.setX(primaryStage.getX() + 30);
                    errorWindow.setY(primaryStage.getY() + 80);
                    errorWindow.show();

                    errorBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            errorWindow.close();
                        }
                    });
                }else {

                    int width = Integer.parseInt(dtfWidth.getText());
                    final int[] it = {0};
                    ArrayList<Class<? extends Value>> ct = new ArrayList<>();

                    Label secondLabel = new Label("Wybierz typy kolejnych kolumn:");


                    secondLabel.setText("Wybierz typ kolumny nr 1:");

                    GridPane secondaryLayout = new GridPane();
                    secondaryLayout.setAlignment(Pos.CENTER);
                    secondaryLayout.setHgap(10);
                    secondaryLayout.setVgap(10);
                    secondaryLayout.setPadding(new Insets(25, 25, 25, 25));
                    secondaryLayout.add(secondLabel, 0, 0);

                    Scene secondScene = new Scene(secondaryLayout, 400, 150);

                    Button addType = new Button("OK");
                    HBox hbAddType = new HBox(10);
                    hbAddType.setAlignment(Pos.BOTTOM_RIGHT);
                    hbAddType.getChildren().add(addType);
                    secondaryLayout.add(hbAddType, 1, 1);

                    ObservableList<String> options =
                            FXCollections.observableArrayList(
                                    "Integer",
                                    "Double",
                                    "Float",
                                    "String",
                                    "Date"
                            );
                    ComboBox typy = new ComboBox(options);
                    secondaryLayout.add(typy, 1, 0);

                    Stage newWindow = new Stage();
                    newWindow.setTitle("Wybór typów kolumn");
                    newWindow.setScene(secondScene);

                    newWindow.initModality(Modality.WINDOW_MODAL);

                    newWindow.initOwner(primaryStage);

                    newWindow.setX(primaryStage.getX() + 30);
                    newWindow.setY(primaryStage.getY() + 80);

                    newWindow.show();

                    addType.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if (typy.getValue() != null) {
                                it[0]++;
                                String val = typy.getValue().toString();
                                if (val == "Integer") ct.add(VInteger.class);
                                if (val == "Double") ct.add(VDouble.class);
                                if (val == "Float") ct.add(VFloat.class);
                                if (val == "String") ct.add(VString.class);
                                if (val == "Date") ct.add(VDatetime.class);
                            }


                            String labeltxt = "Wybierz typ kolumny nr ";
                            labeltxt = labeltxt + Integer.toString(it[0] + 1) + ":";
                            secondLabel.setText(labeltxt);
                            if (it[0] == width) {
                                try {
                                    dtf[0] = new DataFrame(filepath.getText(), ct, true); //wczytywanie dataframe
                                } catch (IOException e1) {
                                    GridPane errorGrid = new GridPane();
                                    errorGrid.setAlignment(Pos.CENTER);
                                    errorGrid.setHgap(10);
                                    errorGrid.setVgap(10);
                                    errorGrid.setPadding(new Insets(25, 25, 25, 25));
                                    Label errorMsg = new Label("Niepoprawna ścieżka do pliku.");
                                    errorGrid.add(errorMsg, 0, 0);
                                    Scene errorScene = new Scene(errorGrid, 350, 100);

                                    Button errorBtn = new Button("OK");
                                    HBox hbErrBtn = new HBox(10);
                                    hbErrBtn.setAlignment(Pos.CENTER);
                                    hbErrBtn.getChildren().add(errorBtn);
                                    errorGrid.add(hbErrBtn, 0, 1);

                                    Stage errorWindow = new Stage();
                                    errorWindow.setTitle("Błąd!");
                                    errorWindow.setScene(errorScene);
                                    errorWindow.initModality(Modality.WINDOW_MODAL);

                                    errorWindow.initOwner(primaryStage);

                                    errorWindow.setX(primaryStage.getX() + 30);
                                    errorWindow.setY(primaryStage.getY() + 80);
                                    errorWindow.show();

                                    errorBtn.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent actionEvent) {
                                            errorWindow.close();
                                        }
                                    });

                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                } catch (InstantiationException e1) {
                                    e1.printStackTrace();
                                } catch (IncorrectWidth incorrectWidth) {

                                    GridPane errorGrid = new GridPane();
                                    errorGrid.setAlignment(Pos.CENTER);
                                    errorGrid.setHgap(10);
                                    errorGrid.setVgap(10);
                                    errorGrid.setPadding(new Insets(25, 25, 25, 25));
                                    Label errorMsg = new Label("Została podania zła ilość kolumn.");
                                    errorGrid.add(errorMsg, 0, 0);
                                    Scene errorScene = new Scene(errorGrid, 350, 100);

                                    Button errorBtn = new Button("OK");
                                    HBox hbErrBtn = new HBox(10);
                                    hbErrBtn.setAlignment(Pos.CENTER);
                                    hbErrBtn.getChildren().add(errorBtn);
                                    errorGrid.add(hbErrBtn, 0, 1);

                                    Stage errorWindow = new Stage();
                                    errorWindow.setTitle("Błąd!");
                                    errorWindow.setScene(errorScene);
                                    errorWindow.initModality(Modality.WINDOW_MODAL);

                                    errorWindow.initOwner(primaryStage);

                                    errorWindow.setX(primaryStage.getX() + 30);
                                    errorWindow.setY(primaryStage.getY() + 80);
                                    errorWindow.show();

                                    errorBtn.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent actionEvent) {
                                            errorWindow.close();
                                        }
                                    });

                                    incorrectWidth.printStackTrace();
                                }
                                newWindow.close();
                            }
                        }
                    });
                }
            }
        });

        Button makePlot = new Button("Wykres");
        HBox hbmakePlot = new HBox(10);
        hbmakePlot.setAlignment(Pos.CENTER);
        hbmakePlot.getChildren().add(makePlot);
        grid.add(hbmakePlot, 0, 3,2,1);

        makePlot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(dtf[0] != null) {
                    GridPane plotGrid = new GridPane();
                    plotGrid.setAlignment(Pos.CENTER);
                    plotGrid.setHgap(10);
                    plotGrid.setVgap(10);
                    plotGrid.setPadding(new Insets(25, 25, 25, 25));

                    Label labelX = new Label("Wartości na osi X:");
                    plotGrid.add(labelX,0,0);

                    ComboBox setX = new ComboBox();
                    for(int i=0;i<dtf[0].cnames.length;i++){
                        setX.getItems().add(dtf[0].cnames[i]);
                    }
                    plotGrid.add(setX,1,0);

                    Label labelY = new Label("Wartości na osi Y:");
                    plotGrid.add(labelY,0,1);

                    ComboBox setY = new ComboBox();
                    for(int i=0;i<dtf[0].cnames.length;i++){
                        if(dtf[0].ctypes.get(i) == VInteger.class || dtf[0].ctypes.get(i) == VDouble.class || dtf[0].ctypes.get(i) == VFloat.class) {
                            setY.getItems().add(dtf[0].cnames[i]);
                        }
                    }
                    plotGrid.add(setY,1,1);

                    Label labelS = new Label("Co pokazać na wykresie:");
                    plotGrid.add(labelS,0,2);

                    ComboBox showType = new ComboBox();
                    showType.getItems().addAll(
                            "min",
                            "max",
                            "mean",
                            "sum"
                    );
                    plotGrid.add(showType,1,2);

                    Button createPlot = new Button("OK");
                    HBox hbCrPlot = new HBox(10);
                    hbCrPlot.setAlignment(Pos.CENTER);
                    hbCrPlot.getChildren().add(createPlot);
                    plotGrid.add(hbCrPlot, 0, 3,2,1);

                    Scene plotScene = new Scene(plotGrid, 400, 250);
                    Stage plotWindow = new Stage();
                    plotWindow.setTitle("Ustawienia wykresu");
                    plotWindow.setScene(plotScene);
                    plotWindow.initModality(Modality.WINDOW_MODAL);
                    plotWindow.initOwner(primaryStage);
                    plotWindow.setX(primaryStage.getX() + 30);
                    plotWindow.setY(primaryStage.getY() + 80);
                    plotWindow.show();

                    createPlot.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            int Xid = -1,Yid = -1;
                            for(int i=0;i<dtf[0].cnames.length;i++){
                                if(setX.getValue().toString() == dtf[0].cnames[i]){
                                    Xid = i;
                                }
                                if(setY.getValue().toString() == dtf[0].cnames[i]){
                                    Yid = i;
                                }
                            }

                            DataFrame plotDt;
                            if(showType.getValue().toString() == "min") {
                                plotDt = dtf[0].groupby(new String[]{dtf[0].cnames[Xid]}).min();
                            }
                            if(showType.getValue().toString() == "max") {
                                plotDt = dtf[0].groupby(new String[]{dtf[0].cnames[Xid]}).max();
                            }
                            if(showType.getValue().toString() == "sum") {
                                plotDt = dtf[0].groupby(new String[]{dtf[0].cnames[Xid]}).sum();
                            }else{
                                plotDt = dtf[0].groupby(new String[]{dtf[0].cnames[Xid]}).mean();
                            }

                            Stage plot = new Stage();
                            plot.setTitle("Wykres");
                            NumberAxis yAxis = new NumberAxis();
                            if(plotDt.ctypes.get(Xid) == VInteger.class || plotDt.ctypes.get(Xid) == VDouble.class || plotDt.ctypes.get(Xid) == VFloat.class){
                                NumberAxis xAxis = new NumberAxis();
                                xAxis.setLabel(setX.getValue().toString());
                                yAxis.setLabel(setY.getValue().toString());
                                LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
                                lineChart.setTitle("Wykres " + setY.getValue().toString() + " od " + setX.getValue().toString());

                                XYChart.Series series = new XYChart.Series();
                                series.setName(setX.getValue().toString() + "(" + setY.getValue().toString() + ")");

                                for(int i = 0;i<plotDt.heigth;i++){
                                    series.getData().add(new XYChart.Data(plotDt.colms[Xid].col.get(i).toNumber(),plotDt.colms[Yid].col.get(i).toNumber()));
                                }

                                Scene pltScene = new Scene(lineChart,800,600);
                                lineChart.getData().add(series);
                                plot.setScene(pltScene);
                                plot.show();
                            }else{
                                CategoryAxis xAxis = new CategoryAxis();
                                xAxis.setLabel(setX.getValue().toString());
                                yAxis.setLabel(setY.getValue().toString());
                                LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);
                                lineChart.setTitle("Wykres " + setX.getValue().toString() + " od " + setY.getValue().toString());

                                XYChart.Series series = new XYChart.Series();
                                series.setName(setX.getValue().toString() + "(" + setY.getValue().toString() + ")");

                                for(int i = 0;i<plotDt.heigth;i++){
                                    series.getData().add(new XYChart.Data(plotDt.colms[Xid].col.get(i).toString(),plotDt.colms[Yid].col.get(i).toNumber()));
                                }

                                Scene pltScene = new Scene(lineChart,800,600);
                                lineChart.getData().add(series);
                                plot.setScene(pltScene);
                                plot.show();
                            }
                        }
                    });

                }else{
                    GridPane errorGrid = new GridPane();
                    errorGrid.setAlignment(Pos.CENTER);
                    errorGrid.setHgap(10);
                    errorGrid.setVgap(10);
                    errorGrid.setPadding(new Insets(25, 25, 25, 25));
                    Label errorMsg = new Label("Brak wczytanej bazy danych.");
                    errorGrid.add(errorMsg,0,0);
                    Scene errorScene = new Scene(errorGrid,350,100);

                    Button errorBtn = new Button("OK");
                    HBox hbErrBtn = new HBox(10);
                    hbErrBtn.setAlignment(Pos.CENTER);
                    hbErrBtn.getChildren().add(errorBtn);
                    errorGrid.add(hbErrBtn, 0, 1);

                    Stage errorWindow = new Stage();
                    errorWindow.setTitle("Błąd!");
                    errorWindow.setScene(errorScene);
                    errorWindow.initModality(Modality.WINDOW_MODAL);

                    errorWindow.initOwner(primaryStage);

                    errorWindow.setX(primaryStage.getX() + 30);
                    errorWindow.setY(primaryStage.getY() + 80);
                    errorWindow.show();

                    errorBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            errorWindow.close();
                        }
                    });
                }


            }
        });

        Button makeQuery = new Button("Zapytania");
        HBox hbmakeQuery = new HBox(10);
        hbmakeQuery.setAlignment(Pos.CENTER);
        hbmakeQuery.getChildren().add(makeQuery);
        grid.add(hbmakeQuery, 0, 4,2,1);

        makeQuery.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(dtf[0] != null){
                    GridPane queryGrid = new GridPane();
                    queryGrid.setAlignment(Pos.CENTER);
                    queryGrid.setHgap(10);
                    queryGrid.setVgap(10);
                    queryGrid.setPadding(new Insets(25, 25, 25, 25));
                    Scene queryScene = new Scene(queryGrid,350,200);

                    Button queryBtn = new Button("OK");
                    HBox hbQBtn = new HBox(10);
                    hbQBtn.setAlignment(Pos.CENTER);
                    hbQBtn.getChildren().add(queryBtn);
                    queryGrid.add(hbQBtn, 0, 2);

                    Label kolL = new Label("Wybierz kolumne:");
                    queryGrid.add(kolL,0,0);

                    ComboBox kol = new ComboBox();
                    for(int i=0;i<dtf[0].cnames.length;i++){
                        kol.getItems().add(dtf[0].cnames[i]);
                    }
                    queryGrid.add(kol,1,0);

                    Label typeL = new Label("Wybierz zapytanie:");
                    queryGrid.add(typeL,0,1);

                    ComboBox type = new ComboBox();
                    type.getItems().addAll(
                            "min",
                            "max",
                            "sum",
                            "mean",
                            "std",
                            "var"
                    );
                    queryGrid.add(type,1,1);

                    Label wynL = new Label("Wynik:");
                    queryGrid.add(wynL,0,3);

                    Text wyn = new Text("0");
                    queryGrid.add(wyn,1,3);

                    Stage queryWindow = new Stage();
                    queryWindow.setTitle("Zapytania");
                    queryWindow.setScene(queryScene);
                    queryWindow.initModality(Modality.WINDOW_MODAL);

                    queryWindow.initOwner(primaryStage);

                    queryWindow.setX(primaryStage.getX() + 30);
                    queryWindow.setY(primaryStage.getY() + 80);
                    queryWindow.show();

                    queryBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            DataFrame dtw = dtf[0];
                            if(type.getValue().toString() == "min"){
                                dtw = dtf[0].groupby().min();
                            }
                            if(type.getValue().toString() == "max"){
                                dtw = dtf[0].groupby().max();
                            }
                            if(type.getValue().toString() == "mean"){
                                dtw = dtf[0].groupby().mean();
                            }
                            if(type.getValue().toString() == "var"){
                                dtw = dtf[0].groupby().var();
                            }
                            if(type.getValue().toString() == "std"){
                                dtw = dtf[0].groupby().std();
                            }
                            if(type.getValue().toString() == "sum"){
                                dtw = dtf[0].groupby().sum();
                            }
                            for(int i=0;i<dtw.cnames.length;i++){
                                if(dtw.cnames[i] == kol.getValue().toString()){
                                    wyn.setText(dtw.colms[i].col.get(0).toString());
                                }
                            }
                        }
                    });
                }else{
                    GridPane errorGrid = new GridPane();
                    errorGrid.setAlignment(Pos.CENTER);
                    errorGrid.setHgap(10);
                    errorGrid.setVgap(10);
                    errorGrid.setPadding(new Insets(25, 25, 25, 25));
                    Label errorMsg = new Label("Brak wczytanej bazy danych.");
                    errorGrid.add(errorMsg,0,0);
                    Scene errorScene = new Scene(errorGrid,350,100);

                    Button errorBtn = new Button("OK");
                    HBox hbErrBtn = new HBox(10);
                    hbErrBtn.setAlignment(Pos.CENTER);
                    hbErrBtn.getChildren().add(errorBtn);
                    errorGrid.add(hbErrBtn, 0, 1);

                    Stage errorWindow = new Stage();
                    errorWindow.setTitle("Błąd!");
                    errorWindow.setScene(errorScene);
                    errorWindow.initModality(Modality.WINDOW_MODAL);

                    errorWindow.initOwner(primaryStage);

                    errorWindow.setX(primaryStage.getX() + 30);
                    errorWindow.setY(primaryStage.getY() + 80);
                    errorWindow.show();

                    errorBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            errorWindow.close();
                        }
                    });
                }
            }
        });

        primaryStage.setScene(new Scene(grid, 500, 300));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

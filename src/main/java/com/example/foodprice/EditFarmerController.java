package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditFarmerController {
    @FXML private TextField tfName, tfPhone, tfNid, tfDistrict, tfUpazila, tfVillage, tfLand, tfAccount, tfCropInput;
    @FXML private ComboBox<String> cbDivision, cbBanking;
    @FXML private FlowPane fpCrops;

    private List<String> cropList = new ArrayList<>();
    private Farmer currentFarmer;
    private int farmerIndex;
    private boolean updateClicked = false;

    @FXML
    public void initialize() {
        cbDivision.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));
        cbBanking.setItems(FXCollections.observableArrayList("বিকাশ", "নগদ", "রকেট", "উপায়", "ব্যাংক অ্যাকাউন্ট"));
    }

    public void setFarmerData(Farmer f, int index) {
        this.currentFarmer = f;
        this.farmerIndex = index;

        tfName.setText(f.getName());
        tfPhone.setText(f.getPhone().replace("📞 ", ""));
        tfNid.setText(f.getNid());
        cbDivision.setValue(f.getDivision());
        tfDistrict.setText(f.getDistrict());
        tfUpazila.setText(f.getUpazila());
        tfVillage.setText(f.getVillage());
        tfLand.setText(f.getLandAmount().replaceAll("[^0-9.]", ""));
        cbBanking.setValue(f.getBanking());
        tfAccount.setText(f.getAccount());

        // Crops লোড করা
        if (f.getCrops() != null && !f.getCrops().isEmpty()) {
            cropList.addAll(Arrays.asList(f.getCrops().split(", ")));
            refreshCropTags();
        }
    }

    @FXML
    private void handleAddCrop() {
        String crop = tfCropInput.getText().trim();
        if (!crop.isEmpty() && !cropList.contains(crop)) {
            cropList.add(crop);
            tfCropInput.clear();
            refreshCropTags();
        }
    }

    private void refreshCropTags() {
        fpCrops.getChildren().clear();
        for (String crop : cropList) {
            HBox tag = new HBox(5);
            tag.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 5 10; -fx-background-radius: 15; -fx-alignment: center;");
            Label label = new Label(crop);
            Button btnRemove = new Button("✕");
            btnRemove.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: #ef4444; -fx-cursor: hand;");
            btnRemove.setOnAction(e -> {
                cropList.remove(crop);
                refreshCropTags();
            });
            tag.getChildren().addAll(label, btnRemove);
            fpCrops.getChildren().add(tag);
        }
    }

    @FXML
    private void handleUpdate() {
        List<Farmer> farmers = DataManager.loadFarmers();

        String allCrops = String.join(", ", cropList);

        Farmer updatedFarmer = new Farmer(
                tfName.getText(), "📞 " + tfPhone.getText(), tfNid.getText(), cbDivision.getValue(),
                tfDistrict.getText(), tfUpazila.getText(), tfVillage.getText(),
                tfLand.getText() + " একর", cbBanking.getValue(), tfAccount.getText(),
                allCrops, currentFarmer.getTotalSales(), currentFarmer.getScore(), currentFarmer.isVerified()
        );

        farmers.set(farmerIndex, updatedFarmer);
        DataManager.saveFarmers(farmers);
        updateClicked = true;
        closeStage();
    }

    public boolean isUpdateClicked() { return updateClicked; }
    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfName.getScene().getWindow()).close(); }
}
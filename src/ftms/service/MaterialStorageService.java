package ftms.service;

import ftms.dao.CourseMaterialDAO;
import ftms.model.CourseMaterial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

public class MaterialStorageService {

    private CourseMaterialDAO materialDAO = new CourseMaterialDAO();

    private Path storageRoot = Paths.get("materials");

    public void saveMaterial(String courseCode, String title, Path sourceFile) throws IOException {
        String code = courseCode.trim();
        Path courseFolder = storageRoot.resolve(code);
        Files.createDirectories(courseFolder);
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getFileName().toString();
        Path targetFile = courseFolder.resolve(fileName);
        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        materialDAO.insert(new CourseMaterial(0, code, title, targetFile.toString(), LocalDate.now()));
    }

    public List<CourseMaterial> getMaterialsForCourse(String courseCode) {
        return materialDAO.findByCourse(courseCode);
    }

    public CourseMaterial getMaterial(int materialId) {
        return materialDAO.findById(materialId);
    }

    public void deleteMaterial(int materialId) throws IOException {
        CourseMaterial material = materialDAO.findById(materialId);
        if (material != null) {
            Files.deleteIfExists(Paths.get(material.getFilePath()));
            materialDAO.deleteById(materialId);
        }
    }
}
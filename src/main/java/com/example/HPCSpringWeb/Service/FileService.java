package com.example.HPCSpringWeb.Service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileService {

    public static List<String> getAllFilenames(String directory) throws IOException {
        try (var paths = Files.list(Paths.get(directory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
//    public static void main(String[] args) {
//        try {
//            List<String> filenames = getAllFilenames("D:/nam3/PBL4/Share/");
//            filenames.forEach(System.out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



}

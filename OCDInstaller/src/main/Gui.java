package main;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXMessageDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Gui extends JPanel implements ActionListener {

    public JCheckBox checkBoxInstall = new JCheckBox("Install Mod Pack (and Forge 1.6.4)");
    public JCheckBox checkBoxUpdate = new JCheckBox("Update Mod Pack");
    public JLabel minecraftPathLabel = new JLabel("Minecraft path: ");
    public TextField minecraftPath = new TextField();
    public JButton buttonInstall = new JButton("Install");
    public JLabel percentLabel = new JLabel("0%");
    public String os = System.getProperty("os.name");
    public Path path;
    public Path forgePath;

    public Gui(){

        //Layout
        setLayout(new GridLayout(6, 7, 5, 10));
        //Button
        buttonInstall.setSize(100, 30);
        buttonInstall.addActionListener(this::actionPerformed);
        //ADD TO PANEL
        add(checkBoxInstall);
        add(checkBoxUpdate);
        add(minecraftPathLabel);
        add(minecraftPath);
        add(buttonInstall);
        add(percentLabel);
        //

        if(os.toLowerCase().contains("windows")){

            System.out.println("Windows");
            path = Paths.get("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\.minecraft\\mods\\CraftingDead1.2.5-OBF.jar");
            forgePath = Paths.get("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\.minecraft\\versions\\1.6.4-Forge9.11.1.1345\\");

        }
        else if(os.toLowerCase().contains("mac")){

            System.out.println("Mac");
            path = Paths.get("Macintosh HD" + System.getProperty("user.name") + "\\Library\\Application Support\\minecraft\\mods\\CraftingDead1.2.5-OBF.jar");
            forgePath = Paths.get("Macintosh HD" + System.getProperty("user.name") + "\\Library\\Application Support\\minecraft\\versions\\1.6.4-Forge9.11.1.1345\\");

        }

        if (Files.exists(path) && Files.exists(forgePath)) {

            checkBoxInstall.setEnabled(false);

        }
        else{

            checkBoxInstall.setEnabled(true);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == buttonInstall){

            if(checkBoxInstall.isSelected() && minecraftPath.getText() != ""){

                JOptionPane.showMessageDialog(null, "Installing This May Take A While! Wait untill 100%!", "OCD: " + "Installing!", JOptionPane.INFORMATION_MESSAGE);
                installModAndForge("http://dl.brad.ac/uploads/craftingdead/reborn/modpack/o-version-125B9.zip", new File(minecraftPath.getText() + "\\mods\\mod.zip"));

            }
            else if(checkBoxUpdate.isSelected() && minecraftPath.getText() != ""){

                updateMod();

            }
            else{

                JOptionPane.showMessageDialog(null, "Nothing Selected or MineCraft Path isn't correct!", "OCD: " + "Error!", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }


    public void installModAndForge(String link, File out){

        downloadMod(link, out);
        unZip( minecraftPath.getText() + "\\mods\\mod.zip",  minecraftPath.getText() + "\\mods\\CraftingDead\\");
        move(minecraftPath.getText() + "\\mods\\CraftingDead\\mods\\CraftingDead1.2.5-OBF.jar", minecraftPath.getText() + "\\mods\\CraftingDead1.2.5-OBF.jar");
        unZip("1.6.4-Forge9.11.1.1345.zip", minecraftPath.getText() + "\\versions\\");
        //copyDir("1.6.4-Forge9.11.1.1345\\" , minecraft_path.getText() + "\\versions\\1.6.4-Forge9.11.1.1345\\");

    }

    public void updateMod(){

    }

    public void downloadMod(String link, File out){
        try {

            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            double fileSize = (double)http.getContentLengthLong();
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            double downloaded = 0.00;
            int read = 0;
            double percentDownload = 0.00;

            while ((read = in.read(buffer, 0, 1024)) >= 0){

                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownload = (downloaded*100)/fileSize;
                String percent = String.format("%.4f", percentDownload);
                percentLabel.setText(String.format("%.4f", percentDownload) + "%");
                System.out.println(percent + "%");

            }
            bout.close();
            in.close();
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public void deleteDirectoryStream(Path path) throws IOException {

        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

    }

    public void move(String SourcePath, String DestPath){
        try {

            Path path_dest = Paths.get(DestPath);

            if(Files.exists(path_dest)){

                deleteDirectoryStream(path_dest);
                Path path = Files.move(Paths.get(SourcePath),Paths.get(DestPath));

            }
            else{

                Path path = Files.move(Paths.get(SourcePath),Paths.get(DestPath));

            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    //Only copies the folder without contents so I commented this out

    /*public void copyDir(String SourcePath, String DestPath){

        Path srcPath = Paths.get(SourcePath);
        Path dstPath = Paths.get(DestPath);

        try {

            if(Files.exists(dstPath)){

                deleteDirectoryStream(dstPath);
                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);

            }
            else{

                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }*/

    public void unZip(String src, String dest){

        /*minecraft_path.getText() + "\\mods\\mod.zip"
        minecraft_path.getText() + "\\mods\\CraftingDead\\*/

        try(ZipFile file = new ZipFile(src))
        {

            FileSystem fileSystem = FileSystems.getDefault();
            //Get file entries
            Enumeration<? extends ZipEntry> entries = file.entries();

            //We will unzip files in this folder
            String uncompressedDirectory = dest;
            Path path = Paths.get(dest);

            if(Files.exists(path)){

                deleteDirectoryStream(path);
                Files.createDirectory(fileSystem.getPath(uncompressedDirectory));
            }

            else{

                Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

            }

            //Iterate over entries
            while (entries.hasMoreElements())
            {

                ZipEntry entry = entries.nextElement();
                //If directory then create a new directory in uncompressed folder

                if (entry.isDirectory())
                {

                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));

                }
                //Else create the file
                else
                {

                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = uncompressedDirectory + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);

                    while (bis.available() > 0)
                    {

                        fileOutput.write(bis.read());

                    }

                    fileOutput.close();
                    System.out.println("Written :" + entry.getName());
                }
            }
        }
        catch(IOException e)
        {

            e.printStackTrace();

        }
    }

}

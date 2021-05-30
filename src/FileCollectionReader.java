import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

// парсит xml и считывает коллекцию в объект класса LinkedHashSet
public class FileCollectionReader {
    File file;
    DataOutputStream outputStream;
    private final Logger logger = Logger.getLogger("server.fileCollectionReader");
    public FileCollectionReader(File file, DataOutputStream outputStream){
        this.outputStream = outputStream;
        this.file = file;
    }

    public LinkedHashSet<Dragon> readCollection(File file) throws ParserConfigurationException, SAXException, IOException, NotMatchOwnerException {
        try {
            file = new File(System.getenv("FILE"));      // проверка на наличие переменной окружения
        } catch (NullPointerException e) {
            logger.info("Cant find env variable");
            outputStream.writeUTF("Cant find env variable");
            throw new NotMatchOwnerException();
        }
        Scanner xmlScanner = null;
        try {
            xmlScanner = new Scanner(file);
            outputStream.flush();
            String xmlString = "";
            while(xmlScanner.hasNext()) {
                xmlString += xmlScanner.nextLine();
            }
            LinkedHashSet<Dragon> set = new LinkedHashSet<>();
            parse(set, xmlString);
            changeIds(set);
            return set;
        } catch (FileNotFoundException e) {   // неправильный путь к файлу или нет доступа на чтение
            if (!file.canRead()) {
                logger.info("Permission to read denied");
                outputStream.writeUTF("Permission to read denied");
            } else {
                logger.info("File not found");
                outputStream.writeUTF("File not found");
            }
            throw new NotMatchOwnerException();
        }
    }


    public void parse(LinkedHashSet<Dragon> set, String xmlString ) throws IOException, SAXException, ParserConfigurationException {
        if (xmlString.length() > 0) {
            // создаем DOM parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))); // File -> String -> byte[] -> BAIS -> Document :))
            NodeList dragonElements = document.getDocumentElement().getElementsByTagName("dragon");
            // в цикле заполняем коллекцию элементами из запаршенного файла
            HashSet<Integer> numberOfErrors= new HashSet<>();         // Чтобы выводить номера элементов с ошибками
            boolean xmlStatus = false;
            for (int i = 0; i < dragonElements.getLength(); i++) {
                Node dragon = dragonElements.item(i);
                NamedNodeMap attributes = dragon.getAttributes();
                Integer id = null;
                try {         // проверяем есть ли айдишники и считываем их
                    id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                } catch (NullPointerException e){
                    id = null;
                } catch (NumberFormatException e) {
                    xmlStatus = true;
                    numberOfErrors.add(i);
                }
                LocalDateTime creationDate = null;
                try {
                    creationDate = LocalDateTime.parse(attributes.getNamedItem("creation_date").getNodeValue());
                } catch (DateTimeParseException e) {
                    xmlStatus = true;
                    numberOfErrors.add(i);
                } catch (NullPointerException e) {
                    creationDate = null;
                }
                try {
                    String name = attributes.getNamedItem("name").getNodeValue();
                    Coordinates coords = new Coordinates(Integer.parseInt(attributes.getNamedItem("coordinates").getNodeValue().split(" ")[0]),
                            Double.parseDouble(attributes.getNamedItem("coordinates").getNodeValue().split(" ")[1]));
                    Long age = Long.parseLong(attributes.getNamedItem("age").getNodeValue());
                    String description = attributes.getNamedItem("description").getNodeValue();
                    Double wingspan = Double.parseDouble(attributes.getNamedItem("wingspan").getNodeValue());
                    String stringDragonType = attributes.getNamedItem("type").getNodeValue();
                    DragonType dragonType = dragonTypeFromFile(stringDragonType);
                    DragonCave cave = new DragonCave(Integer.parseInt(attributes.getNamedItem("cave").getNodeValue().split(" ")[0]),
                            Double.parseDouble(attributes.getNamedItem("cave").getNodeValue().split(" ")[1]));
                    set.add(new Dragon(id, name, coords, creationDate, age, description, wingspan, dragonType, cave, "somebody"));
                } catch (Exception e) {
                    xmlStatus = true;
                    numberOfErrors.add(i);
                }
            }
            if (xmlStatus) {
                String numbers = "";
                for (Integer integer : numberOfErrors) {
                    numbers = numbers + integer + " ";
                }
                System.out.println("Invalid fields of elements were found. These elements will not be added to collection: " + numbers);
            }
        }
    }

    public static DragonType dragonTypeFromFile(String type){
        int exceptionStatus = 0;
        DragonType dragonType = null;
        while (exceptionStatus == 0){
            switch (type){
                case ("UNDERGROUND"):
                    dragonType = DragonType.UNDERGROUND;
                    exceptionStatus = 1;
                    break;
                case ("AIR"):
                    dragonType = DragonType.AIR;
                    exceptionStatus = 1;
                    break;
                case ("FIRE"):
                    dragonType = DragonType.FIRE;
                    exceptionStatus = 1;
                    break;
                default:
                    throw new NumberFormatException();
            }
        }
        return dragonType;
    }
    public static void changeIds(LinkedHashSet<Dragon> set){
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (Dragon dragon : set) {
            if (!ids.add(dragon.getId())) {
                dragon.setId(new Random().nextInt());
            }
        }
    }

}


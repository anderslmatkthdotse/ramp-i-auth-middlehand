package ramp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ramp.auth.Rest.OutGoing.SuperUser.UserOutGoing;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
public class AuthApplication {

    @Autowired
    private UserOutGoing userOutGoing;

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


    @Bean
    public Docket swaggerConfig(){
        return new Docket(DocumentationType.SWAGGER_2).select()
                .paths(PathSelectors.ant("/admin/**"))
                .apis(RequestHandlerSelectors.basePackage("ramp.auth"))
                .build()
                .apiInfo(apiInfo());
    }
    private ApiInfo apiInfo(){
        return new ApiInfo(
                "Ramp-auth-middlehand",
                "Dokumentation rörande hantering av ramp användare och organisationer",
                "0.0.1", "",
                new Contact("Gustav Kavtaradze","","guek@kth.se"),
                "","",
                Collections.emptyList());
    }

//    @Bean
//    void printXML() throws JAXBException {
//        JAXBContext context = JAXBContext.newInstance(UserDTO.class);
//
//        Marshaller marshaller = context.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
//
//        File file = new File("src/main/resources/" + "user.xml");
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setFirstName("Spring");
//        userDTO.setLastName("Boot");
//
//        List<CredentialDTO> credentialDTOList = new ArrayList<>();
//        CredentialDTO credentialDTO = new CredentialDTO();
//        credentialDTO.setTemporary(false);
//        credentialDTO.setValue("Password");
//        credentialDTOList.add(credentialDTO);
//        userDTO.setCredentials(credentialDTOList);
//
//        List<String> groupList = new ArrayList<>();
//        groupList.add("/KTH/Flemmingsberg/Datateknik");
//        groupList.add("/KTH");
//        userDTO.setGroups(groupList);
//
//        marshaller.marshal(userDTO,file);
//    }

}


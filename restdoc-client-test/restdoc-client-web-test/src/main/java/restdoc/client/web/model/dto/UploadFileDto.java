package restdoc.client.web.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class UploadFileDto {

    private String oid;

    private MultipartFile file;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

public class DownloadFileVO {
    private String name;
    private String url;



    /**
     * 파일 다운로드 관리자 전용 데이터 VO
     * @param name 파일 이름
     * @param url 파일 다운로드 URL
     */
    public DownloadFileVO(String name, String url) {
        this.name = name;
        this.url = url;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

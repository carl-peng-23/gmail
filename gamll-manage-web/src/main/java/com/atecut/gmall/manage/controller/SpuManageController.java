package com.atecut.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atecut.gmall.bean.BaseSaleAttr;
import com.atecut.gmall.bean.SpuInfo;
import com.atecut.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class SpuManageController {

    @Value("${fileServer.url}")
    String fileUrl;

    @Reference
    ManageService manageService;

    @RequestMapping("spuList")
    public List<SpuInfo> spuList(SpuInfo spuInfo) {
        return manageService.getSpuInfoList(spuInfo);
    }

    //fileUpload
    @RequestMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrl = fileUrl;
        if (file != null && file.getSize() > 0) {
            String file1 = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(file1);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String orginalFilename = file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(orginalFilename, ".");
            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            //String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);
            for (int i = 0; i < upload_file.length; i++) {
                String s = upload_file[i];
                imgUrl += "/" + s;
            }
        }
        return imgUrl;
    }

    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList() {
        return manageService.getBaseSaleAttrList();
    }

    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
    }

}

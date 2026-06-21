package com.exercise.admin.controller;

import com.exercise.admin.biz.MailTemplateAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.MailTemplateUpsertDTO;
import com.exercise.campus.entity.vo.MailTemplateVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mailTemplate/admin")
public class MailTemplateAdminController extends BaseController {

    @Autowired
    private MailTemplateAdminBiz biz;

    @PostMapping("/loadDataList")
    public ResponseVO<List<MailTemplateVO>> loadDataList() {
        return success(biz.list());
    }

    @PostMapping("/getById")
    public ResponseVO<MailTemplateVO> getById(@RequestBody MailTemplateUpsertDTO dto) {
        return success(biz.getById(dto.getTemplateId()));
    }

    @PostMapping("/upsert")
    public ResponseVO<MailTemplateVO> upsert(@RequestBody MailTemplateUpsertDTO dto) {
        return success(biz.upsert(dto));
    }

    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody MailTemplateUpsertDTO dto) {
        biz.delete(dto.getTemplateId());
        return success();
    }
}
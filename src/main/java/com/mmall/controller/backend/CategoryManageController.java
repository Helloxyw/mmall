package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;

import javax.servlet.http.HttpSession;

/**
 * @ClassName CategoryManageController
 * @Description TODO
 * @Author root
 * @Date 18-11-12 下午10:48
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "the user is not inline,need login ");
        }
        //check is admin
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //is admin
            //add handle category logic
            return iCategoryService.addCategory(categoryName, parentId);

        } else {
            return ServerResponse.createByErrorMessage("no authority to operate, requiring administrator privileges");
        }
    }


    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "the user is not inline,need login ");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("no authority to operate, requiring administrator privileges");
        }
    }


    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,
                                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "the user is not inline,need login ");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //select children node's category information and not recursive,keep parallel
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("no authority to operate, requiring administrator privileges");
        }
    }

    public ServerResponse
}

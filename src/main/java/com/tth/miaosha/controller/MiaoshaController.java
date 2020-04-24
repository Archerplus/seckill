package com.tth.miaosha.controller;

import com.tth.miaosha.access.AccessLimit;
import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.domain.MiaoshaOrder;
import com.tth.miaosha.rabbitmq.MQSender;
import com.tth.miaosha.rabbitmq.MiaoshaMessage;
import com.tth.miaosha.redis.GoodsKey;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.result.CodeMsg;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.GoodsService;
import com.tth.miaosha.service.MiaoShaUserService;
import com.tth.miaosha.service.MiaoshaService;
import com.tth.miaosha.service.OrderService;
import com.tth.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;


    private Map<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
//    @RequestMapping("/do_miaosha")
//    public String list(Model model, MiaoShaUser user, @RequestParam("goodsId")Long goodsId){
//        System.out.println("world");
//        model.addAttribute("user",user);
//        if(user == null){
//            return "login";
//        }
//        //判断商品是否有库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getGoodsStock();
//        if(stock <= 0){
//            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
//            return "miaosha_fail";
//        }
//        //判断是否已经秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);//判断用户是否已经秒杀到商品
//        System.out.println("userId: " + user.getId() + ", goodsId: " + goodsId);
//        if(order != null){
//            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
//            return "miaosha_fail";
//        }
//        //减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goods);
//        return "order_detail";
//    }


    /**
     * GET、POST有什么区别?
     * GET是幂等的，代表的是从服务端获取数据,无论调用多少次，
     * 对服务端产生的结果是一样的，不会对服务端数据产生任何影响
     * POST是向服务端提交数据，对服务端的数据发生了变化
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoShaUser user,
                                   @RequestParam("goodsId")Long goodsId,
                                   @PathVariable("path")String path){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check =  miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //利用内存的一个标记，减少我们的一个redis访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//0就代表是排队中
        /*
        //判断商品是否有库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);//判断用户是否已经秒杀到商品
        System.out.println("userId: " + user.getId() + ", goodsId: " + goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
        return Result.success(orderInfo);

         */
    }

    /**
     * 系统初始化所做的一些事情
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return ;
        }
        //将商品的库存加载到redis中去
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    /***
     * orderId：成功
     * -1：秒杀失败
     * 0：排队中
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoShaUser user, @RequestParam("goodsId")Long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        Long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=20,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,MiaoShaUser user,
                                         @RequestParam("goodsId")Long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response,Model model, MiaoShaUser user,
                                         @RequestParam("goodsId")Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoshaService.createVerifyCode(user,goodsId);
        try{
            OutputStream out = response.getOutputStream();
            //将图片写到输出流里面去
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}

<table style="border:3px solid #D9F4FF;width:594px;" cellspacing="0" cellpadding="0">
  <tr>
    <td><table style="border:1px solid #65C3D6;font-size:14px;" cellspacing="0" cellpadding="0">
        <tr>
            <table cellspacing="0" cellpadding="0" border="0" style="width:100%;">
              <tr>
                <td style="padding:25px 30px;"><table cellspacing="0" cellpadding="0" border="0" style="background:#E0EEEE;border:1px solid #F8F3D6;width:100%;">
                    <tr>
                    <td style="color:#4682B4;padding:20px 0 20px 20px;">
                    <#if type==1>
                    账户: <b><font color="red">${username}</font></b>
                      初始化HttpClient出现验证码，请点击以下地址<a href="${url}" target="_blank">${url}</a>进行验证码输入
                    <#elseif type==2>
                   	 抓取关键字:<b><font color="red">${username}</font></b>
                      出现验证码，请输入验证码!
                    <#elseif type==3>
                    账户: <b><font color="red">${username}</font></b>
                      授权应用出现验证码，请点击以下地址<a href="${url}" target="_blank">${url}</a>进行验证码输入
                    <#elseif type==4>            
                    	${username}                    
                    </#if>
                      
                      来源${server}</td>
                    </tr>

 
                  </table>
                  <table cellspacing="0" cellpadding="0" border="0" style="font-size:12px;">
                   
                  </table>
                  <table border="0" cellspacing="0" cellpadding="0" style="color:#999;margin-top:20px;font-size:12px;width:100%;">
                    <tr>
                    <td style="padding-top:8px;padding-right:0px;padding-bottom:10px;">感谢你的使用！</td>
                    </tr>
 
                    <tr>
                      <td style="padding:10px 0 0;border-top:1px dotted #9F9F9F;line-height:1.5;">北京合力金桥软件技术有限责任公司微博团队<br  />
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
 
      </table></td>
  </tr>
</table>
لإنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت أداة مراقبة الشبكات Cacti Network Monitoring عليه دون الحاجة إلى إدخال اسم مستخدم وكلمة مرور، يمكنك اتباع الخطوات التالية:

### المتطلبات:
1. جهاز يعمل بنظام Linux (أو جهاز افتراضي يعمل بنظام Linux على Windows).
2. أدوات Buildroot المثبتة.
3. اتصال بالإنترنت لتنزيل الحزم الضرورية.

### الخطوات:

#### 1. إعداد بيئة Buildroot:
أولاً، تحتاج إلى إعداد بيئة Buildroot على نظام Linux.

##### على نظام Linux:
1. تثبيت Git لتنزيل Buildroot:
   ```bash
   sudo apt-get update
   sudo apt-get install git
   ```

2. تنزيل Buildroot:
   ```bash
   git clone https://github.com/buildroot/buildroot.git
   cd buildroot
   ```

3. إعداد Buildroot للأجهزة 32 بت:
   ```bash
   make menuconfig
   ```

#### 2. إعداد Buildroot:
داخل واجهة `menuconfig`، قم بتكوين Buildroot كما يلي:

- **Target Architecture**: اختر `x86`.
- **Target Architecture Variant**: اختر `i686` (لأجهزة 32 بت).
- **Toolchain**: اختر التول تشين المناسبة لنظامك.
- **Target Packages -> Networking applications**: حدد `apache`, `mysql`, `php`, `rrdtool`، و `net-snmp` (Cacti يعتمد على هذه الحزم).

#### 3. إضافة Cacti إلى Buildroot:
يمكنك إضافة Cacti إلى Buildroot عن طريق كتابة سكريبت حزمة مخصصة.

1. إنشاء دليل للباقة المخصصة:
   ```bash
   mkdir -p package/cacti
   ```

2. إنشاء ملف تعريف الحزمة:
   أنشئ ملفًا باسم `cacti.mk` داخل دليل `package/cacti` يحتوي على:
   ```makefile
   CACTI_VERSION = 1.2.19
   CACTI_SITE = https://www.cacti.net/downloads/cacti-$(CACTI_VERSION)
   CACTI_SOURCE = $(CACTI_SITE).tar.gz

   $(eval $(call DOWNLOAD,cacti))

   define CACTI_EXTRACT_CMDS
       $(TAR) -xvf $(@D)/cacti-$(CACTI_VERSION).tar.gz -C $(TARGET_DIR)/opt
       mv $(TARGET_DIR)/opt/cacti-$(CACTI_VERSION) $(TARGET_DIR)/opt/cacti
   endef

   define CACTI_INSTALL_INIT_SYSV
       $(INSTALL) -D -m 0755 package/cacti/cacti-setup.sh $(TARGET_DIR)/usr/bin/cacti-setup.sh
   endef

   define CACTI_POST_INSTALL_CMDS
       $(TARGET_DIR)/usr/bin/cacti-setup.sh
   endef

   $(eval $(generic-package))
   ```

3. إنشاء ملف إعدادات الحزمة:
   أنشئ ملفًا باسم `Config.in` داخل دليل `package/cacti` يحتوي على:
   ```text
   config BR2_PACKAGE_CACTI
       bool "cacti"
       help
         Cacti is a complete network graphing solution designed to harness the power of RRDTool's data storage and graphing functionality.
   ```

4. تحديث قائمة الحزم:
   في ملف `package/Config.in`، أضف السطر التالي:
   ```text
   source "package/cacti/Config.in"
   ```

#### 4. إعداد Cacti للتثبيت التلقائي:
لإعداد Cacti بحيث يتم تثبيته وتشغيله تلقائيًا دون الحاجة إلى إدخال اسم مستخدم وكلمة مرور:

1. إنشاء سكريبت للإعداد التلقائي:
   أنشئ ملفًا باسم `cacti-setup.sh` داخل دليل `package/cacti` يحتوي على:
   ```bash
   #!/bin/sh
   mysql -e "CREATE DATABASE cacti;"
   mysql -e "CREATE USER 'cacti'@'localhost' IDENTIFIED BY 'password';"
   mysql -e "GRANT ALL PRIVILEGES ON cacti.* TO 'cacti'@'localhost';"
   mysql -e "FLUSH PRIVILEGES;"
   cd /opt/cacti
   cp include/config.php include/config.php.orig
   sed -i "s/\$database_username = 'cactiuser';/\$database_username = 'cacti';/g" include/config.php
   sed -i "s/\$database_password = 'cactiuser';/\$database_password = 'password';/g" include/config.php
   php install/index.php --install
   ```

2. تعديل ملف `cacti.mk` لتضمين السكريبت:
   ```makefile
   CACTI_VERSION = 1.2.19
   CACTI_SITE = https://www.cacti.net/downloads/cacti-$(CACTI_VERSION)
   CACTI_SOURCE = $(CACTI_SITE).tar.gz

   $(eval $(call DOWNLOAD,cacti))

   define CACTI_EXTRACT_CMDS
       $(TAR) -xvf $(@D)/cacti-$(CACTI_VERSION).tar.gz -C $(TARGET_DIR)/opt
       mv $(TARGET_DIR)/opt/cacti-$(CACTI_VERSION) $(TARGET_DIR)/opt/cacti
   endef

   define CACTI_INSTALL_INIT_SYSV
       $(INSTALL) -D -m 0755 package/cacti/cacti-setup.sh $(TARGET_DIR)/usr/bin/cacti-setup.sh
   endef

   define CACTI_POST_INSTALL_CMDS
       $(TARGET_DIR)/usr/bin/cacti-setup.sh
   endef

   $(eval $(generic-package))
   ```

#### 5. بناء النظام:
بعد إضافة Cacti وتحديد إعدادات Buildroot، قم ببناء النظام:
```bash
make
```

ستنتج العملية صورة قرص جاهزة للاستخدام. يمكن العثور على الصورة في دليل `output/images`.

#### 6. إعداد محرك أقراص USB قابل للتمهيد:
استخدم برنامج مثل Rufus أو UNetbootin على نظام Windows لتحميل الصورة على محرك أقراص USB.

1. تحميل وتثبيت Rufus:
   [تحميل Rufus](https://rufus.ie/)
2. إدراج محرك أقراص USB في جهازك.
3. تشغيل Rufus واختيار محرك الأقراص USB الخاص بك.
4. اختيار صورة Buildroot التي تم إنشاؤها (`.img` أو `.iso`) من دليل `output/images`.
5. الضغط على "Start" لبدء عملية إنشاء محرك أقراص USB القابل للتمهيد.

#### 7. تثبيت النظام على الجهاز:
1. إدراج محرك أقراص USB القابل للتمهيد في جهاز الكمبيوتر المستهدف.
2. إعادة تشغيل الجهاز والدخول إلى BIOS/UEFI لتغيير ترتيب التمهيد وجعل الجهاز يقلع من محرك أقراص USB.
3. حفظ التغييرات وإعادة التشغيل.

سيقوم الجهاز بالتمهيد من محرك أقراص USB وتشغيل النظام الذي قمت بإنشائه باستخدام Buildroot مع أداة مراقبة الشبكات Cacti المثبتة مسبقًا والمعدة للتشغيل دون الحاجة إلى إدخال اسم مستخدم وكلمة مرور.

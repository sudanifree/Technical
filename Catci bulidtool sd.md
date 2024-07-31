لإنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت أداة مراقبة الشبكات Cacti عليه، يمكنك اتباع الخطوات التالية:

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

- **Target Architecture**: اختر `x86_64`.
- **Target Architecture Variant**: اختر `i686` (لأجهزة 32 بت).
- **Toolchain**: اختر التول تشين المناسبة لنظامك.
- **Target Packages -> Networking applications**: حدد `apache`, `php`, و `mariadb` (Cacti يعتمد على هذه الحزم).

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
   CACTI_SOURCE = https://www.cacti.net/downloads/cacti-$(CACTI_VERSION).tar.gz

   $(eval $(call DOWNLOAD,cacti))

   define CACTI_EXTRACT_CMDS
       $(TAR) -xvf $(@D)/$(CACTI_VERSION).tar.gz -C $(TARGET_DIR)/opt
   endef

   define CACTI_INSTALL_INIT_SYSV
       $(INSTALL) -D -m 0755 package/cacti/cacti.init $(TARGET_DIR)/etc/init.d/S99cacti
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

#### 4. بناء النظام:
بعد إضافة Cacti وتحديد إعدادات Buildroot، قم ببناء النظام:
```bash
make
```

ستنتج العملية صورة قرص جاهزة للاستخدام. يمكن العثور على الصورة في دليل `output/images`.

#### 5. إعداد محرك أقراص USB قابل للتمهيد:
استخدم برنامج مثل Rufus أو UNetbootin على نظام Windows لتحميل الصورة على محرك أقراص USB.

1. تحميل وتثبيت Rufus:
   [تحميل Rufus](https://rufus.ie/)
2. إدراج محرك أقراص USB في جهازك.
3. تشغيل Rufus واختيار محرك الأقراص USB الخاص بك.
4. اختيار صورة Buildroot التي تم إنشاؤها (`.img` أو `.iso`) من دليل `output/images`.
5. الضغط على "Start" لبدء عملية إنشاء محرك أقراص USB القابل للتمهيد.

#### 6. تثبيت النظام على الجهاز:
1. إدراج محرك أقراص USB القابل للتمهيد في جهاز الكمبيوتر المستهدف.
2. إعادة تشغيل الجهاز والدخول إلى BIOS/UEFI لتغيير ترتيب التمهيد وجعل الجهاز يقلع من محرك أقراص USB.
3. حفظ التغييرات وإعادة التشغيل.

سيقوم الجهاز بالتمهيد من محرك أقراص USB وتشغيل النظام الذي قمت بإنشائه باستخدام Buildroot مع أداة مراقبة الشبكات Cacti.

بهذه الطريقة، يمكنك إنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت نظام Cacti عليه لمراقبة الشبكات.

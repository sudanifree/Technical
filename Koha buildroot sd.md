لإنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت نظام إدارة المكتبات Koha عليه، يمكنك اتباع الخطوات التالية:

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
- **Target Packages -> Libraries -> Graphics**: اختر مكتبة `libX11` إذا كنت بحاجة إلى دعم واجهة المستخدم الرسومية.
- **Target Packages -> Networking applications**: حدد `apache` و `mysql` أو `mariadb` (Koha يعتمد على MySQL/MariaDB).

#### 3. إضافة Koha إلى Buildroot:
يمكنك إضافة Koha إلى Buildroot عن طريق كتابة سكريبت حزمة مخصصة.

1. إنشاء دليل للباقة المخصصة:
   ```bash
   mkdir -p package/koha
   ```

2. إنشاء ملف تعريف الحزمة:
   أنشئ ملفًا باسم `koha.mk` داخل دليل `package/koha` يحتوي على:
   ```makefile
   KOHA_VERSION = 20.05
   KOHA_SOURCE = https://download.koha-community.org/koha-$(KOHA_VERSION).tar.gz

   $(eval $(call DOWNLOAD,koha))

   define KOHA_EXTRACT_CMDS
       $(TAR) -xvf $(@D)/$(KOHA_VERSION).tar.gz -C $(TARGET_DIR)/opt
   endef

   $(eval $(generic-package))
   ```

3. إنشاء ملف إعدادات الحزمة:
   أنشئ ملفًا باسم `Config.in` داخل دليل `package/koha` يحتوي على:
   ```text
   config BR2_PACKAGE_KOHA
       bool "koha"
       help
         Koha is a library management system.
   ```

4. تحديث قائمة الحزم:
   في ملف `package/Config.in`، أضف السطر التالي:
   ```text
   source "package/koha/Config.in"
   ```

#### 4. بناء النظام:
بعد إضافة Koha وتحديد إعدادات Buildroot، قم ببناء النظام:
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

سيقوم الجهاز بالتمهيد من محرك أقراص USB وتشغيل النظام الذي قمت بإنشائه باستخدام Buildroot مع نظام إدارة المكتبات Koha.

بهذه الطريقة، يمكنك إنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت نظام Koha عليه.

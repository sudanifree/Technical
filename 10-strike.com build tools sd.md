لإنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت أدوات من 10-Strike على هذا النظام، تحتاج إلى اتباع خطوات إعداد Buildroot وإضافة حزم مخصصة لتلك الأدوات. 

بما أن أدوات 10-Strike عادة تكون متوفرة لنظام Windows وليست مباشرة لنظام Linux، فإنك ستحتاج إلى نظام Linux يحتوي على بيئة Wine لتشغيل برامج Windows. 

إليك الخطوات:

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
- **Target Packages -> System tools**: حدد `wine` لتثبيت Wine (لتشغيل برامج Windows).
- **Target Packages -> Networking applications**: حدد أي حزم أخرى تحتاجها لدعم الشبكات.

#### 3. إضافة 10-Strike إلى Buildroot:
لإضافة أدوات 10-Strike، سنستخدم Wine لتشغيل هذه الأدوات.

1. إنشاء دليل للباقة المخصصة:
   ```bash
   mkdir -p package/10strike
   ```

2. إنشاء ملف تعريف الحزمة:
   أنشئ ملفًا باسم `10strike.mk` داخل دليل `package/10strike` يحتوي على:
   ```makefile
   10STRIKE_VERSION = your_version
   10STRIKE_SOURCE = your_download_link

   $(eval $(call DOWNLOAD,10strike))

   define 10STRIKE_EXTRACT_CMDS
       $(UNZIP) $(@D)/$(10STRIKE_VERSION).zip -d $(TARGET_DIR)/opt/10strike
   endef

   define 10STRIKE_INSTALL_CMDS
       $(INSTALL) -D -m 0755 package/10strike/10strike.sh $(TARGET_DIR)/usr/bin/10strike
   endef

   $(eval $(generic-package))
   ```

3. إنشاء ملف إعدادات الحزمة:
   أنشئ ملفًا باسم `Config.in` داخل دليل `package/10strike` يحتوي على:
   ```text
   config BR2_PACKAGE_10STRIKE
       bool "10strike"
       help
         10-Strike Network Tools.
   ```

4. إنشاء سكربت تشغيل:
   أنشئ ملفًا باسم `10strike.sh` داخل دليل `package/10strike` يحتوي على:
   ```bash
   #!/bin/sh
   exec wine /opt/10strike/your_10strike_tool.exe "$@"
   ```

5. تحديث قائمة الحزم:
   في ملف `package/Config.in`، أضف السطر التالي:
   ```text
   source "package/10strike/Config.in"
   ```

#### 4. بناء النظام:
بعد إضافة 10-Strike وتحديد إعدادات Buildroot، قم ببناء النظام:
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

سيقوم الجهاز بالتمهيد من محرك أقراص USB وتشغيل النظام الذي قمت بإنشائه باستخدام Buildroot مع أدوات 10-Strike المشغلة عبر Wine.

بهذه الطريقة، يمكنك إنشاء نظام تشغيل مخصص للأجهزة 32 بت باستخدام Buildroot وتثبيت أدوات 10-Strike عليه.

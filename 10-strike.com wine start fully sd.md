لإنشاء نظام تشغيل مخصص باستخدام Buildroot للأجهزة 32 بت والذي يتضمن برنامج Network Inventory Explorer من 10-Strike دون الحاجة إلى إدخال اسم مستخدم وكلمة مرور، يجب أن نأخذ في الاعتبار أن Buildroot هو أداة لإنشاء أنظمة تشغيل لينكس خفيفة الوزن. لا يمكن تشغيل البرامج المخصصة لأنظمة Windows مباشرةً في بيئة لينكس. ومع ذلك، يمكننا إعداد نظام تشغيل لينكس مخصص يحتوي على أدوات مماثلة أو تشغيل برامج Windows باستخدام Wine.

### خطوات إعداد Buildroot لنظام تشغيل مخصص

#### 1. إعداد بيئة Buildroot

##### تثبيت المتطلبات الأساسية

```bash
sudo apt-get update
sudo apt-get install git build-essential ncurses-dev
```

##### تنزيل Buildroot

```bash
git clone https://github.com/buildroot/buildroot.git
cd buildroot
```

#### 2. تكوين Buildroot

##### فتح واجهة تكوين Buildroot

```bash
make menuconfig
```

##### إعداد الخيارات في واجهة `menuconfig`

- **Target options:**
  - `Target Architecture`: اختر `x86`
  - `Target Architecture Variant`: اختر `i686` (للأجهزة 32 بت)

- **Toolchain:**
  - اختر التول تشين المناسبة لنظامك.

- **Target packages:**
  - اذهب إلى `Interpreter languages and scripting`
    - فعل `python3` (اختياري إذا كنت بحاجة إلى Python)
  - اذهب إلى `Networking applications`
    - فعل الحزم المطلوبة للمراقبة (مثل `net-tools`, `iftop`, `nmap`, إلخ).

- **Filesystem images:**
  - فعل `ext2/3/4 root filesystem`
  - اختر `ext4` كنوع نظام الملفات

#### 3. إعداد الدخول التلقائي

لتفعيل الدخول التلقائي بدون الحاجة لإدخال اسم مستخدم وكلمة مرور، يمكنك استخدام `inittab` لتشغيل برنامجك أو سكريبت عند بدء التشغيل.

##### إضافة سكريبت بدء التشغيل

إنشاء ملف سكريبت في دليل `overlay` الخاص بك (سيكون جزء من نظام الملفات الجاهز).

مثال:
```bash
mkdir -p overlay/etc/init.d
touch overlay/etc/init.d/S99start-app
chmod +x overlay/etc/init.d/S99start-app
```

محتوى `S99start-app` يمكن أن يكون:
```bash
#!/bin/sh
# Start your application or script here
/usr/bin/wine /path/to/network-inventory-setup.exe
```

#### 4. بناء النظام

بعد الانتهاء من التكوين، قم ببناء النظام:
```bash
make
```

ستنتج العملية صورة جاهزة للاستخدام. يمكنك العثور على الصورة في `output/images`.

#### 5. إعداد محرك أقراص USB قابل للتمهيد

##### استخدام Rufus على نظام Windows

1. تحميل وتثبيت Rufus: [تحميل Rufus](https://rufus.ie/)
2. إدراج محرك أقراص USB في جهازك.
3. تشغيل Rufus واختيار محرك الأقراص USB الخاص بك.
4. اختيار صورة Buildroot التي تم إنشاؤها (`.img` أو `.iso`) من دليل `output/images`.
5. الضغط على "Start" لبدء عملية إنشاء محرك أقراص USB القابل للتمهيد.

#### 6. إعداد نظام التشغيل

##### تشغيل نظام التشغيل من محرك الأقراص USB

1. إدراج محرك أقراص USB القابل للتمهيد في جهاز الكمبيوتر المستهدف.
2. إعادة تشغيل الجهاز والدخول إلى BIOS/UEFI لتغيير ترتيب التمهيد وجعل الجهاز يقلع من محرك أقراص USB.
3. حفظ التغييرات وإعادة التشغيل.

### إعداد Wine لتشغيل برامج Windows

لتشغيل برنامج Windows مثل Network Inventory Explorer باستخدام Wine:

#### تثبيت Wine

تأكد من أن نظامك يحتوي على Wine مثبتًا:

```bash
# Example for Debian-based systems
sudo apt-get install wine
```

#### تشغيل البرنامج باستخدام Wine

ضمن سكريبت بدء التشغيل الذي تم إنشاؤه في `overlay/etc/init.d/S99start-app`، أضف السطر التالي لتشغيل البرنامج:

```bash
#!/bin/sh
# Start your application or script here
/usr/bin/wine /path/to/network-inventory-setup.exe
```

بهذه الطريقة، عند بدء تشغيل الجهاز، سيتم تشغيل نظام التشغيل الخاص بك دون الحاجة لإدخال اسم مستخدم وكلمة مرور، وسيتم تشغيل برنامج Network Inventory Explorer تلقائيًا باستخدام Wine.

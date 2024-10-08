OpenWrt هو نظام تشغيل مخصص يعتمد على Linux مصمم لتشغيل أجهزة الشبكات مثل أجهزة التوجيه (routers) وأجهزة التبديل (switches). لتثبيت أو إنشاء firmware جديد لجهازك باستخدام OpenWrt على نظام Linux، يجب اتباع بعض الخطوات الأساسية. إليك كيفية القيام بذلك:

### الأدوات المطلوبة:
1. **جهاز كمبيوتر يعمل بنظام Linux.**
2. **كابل USB لتوصيل الجهاز بالكمبيوتر.**
3. **الأدوات الضرورية لتثبيت OpenWrt.**

### الخطوات:

#### 1. تثبيت الأدوات الضرورية:
لتثبيت الأدوات اللازمة لبناء OpenWrt، قم بتشغيل الأوامر التالية في الطرفية (terminal):

```sh
sudo apt-get update
sudo apt-get install git build-essential libncurses5-dev gawk gettext unzip file libssl-dev wget python3
```

#### 2. تنزيل كود المصدر لـ OpenWrt:
لتنزيل الكود المصدر لـ OpenWrt، استخدم Git لتنزيل المستودع الرسمي:

```sh
git clone https://git.openwrt.org/openwrt/openwrt.git
cd openwrt
```

#### 3. إعداد بيئة البناء:
قم بتكوين الإعدادات اللازمة لبناء OpenWrt:

```sh
./scripts/feeds update -a
./scripts/feeds install -a
```

ثم قم بتشغيل القائمة لتكوين الخيارات:

```sh
make menuconfig
```

#### 4. تكوين الإعدادات:
في القائمة التفاعلية، قم بتحديد المعمارية (target architecture) ونموذج الجهاز (device model) المطلوب. يمكنك العثور على النموذج المناسب لجهازك من خلال التوثيق الرسمي لـ OpenWrt.

#### 5. بناء الـ Firmware:
لبناء الـ Firmware، قم بتشغيل الأمر التالي:

```sh
make
```

هذه العملية قد تستغرق وقتًا بناءً على سرعة جهاز الكمبيوتر الخاص بك.

#### 6. تثبيت الـ Firmware على الجهاز:
بمجرد الانتهاء من بناء الـ Firmware، يجب أن تجد الملفات في المجلد `bin/targets`. لاستخدام USB لتثبيت الـ Firmware على جهازك، يجب استخدام أداة معينة تتوافق مع جهازك.

#### 7. استخدام أداة الفلاش المناسبة:
إذا كنت تستخدم جهاز توجيه أو جهاز مشابه، قد تحتاج إلى أداة مثل `sysupgrade` أو `mtd` لتثبيت الـ Firmware الجديد. تأكد من أنك تعرف كيفية الدخول إلى وضع الاسترداد أو وضع التحديث في جهازك.

### مثال على تثبيت الـ Firmware باستخدام `sysupgrade`:

1. **نقل ملف الـ Firmware إلى الجهاز:**
   يمكنك نقل ملف الـ Firmware باستخدام SCP أو أي أداة نقل ملفات أخرى:

   ```sh
   scp /path/to/your/openwrt-firmware.bin root@192.168.1.1:/tmp/
   ```

2. **تثبيت الـ Firmware:**
   قم بتسجيل الدخول إلى جهازك باستخدام SSH:

   ```sh
   ssh root@192.168.1.1
   ```

   ثم قم بتثبيت الـ Firmware:

   ```sh
   sysupgrade /tmp/openwrt-firmware.bin
   ```

### ملاحظات هامة:
- **نسخ احتياطي للبيانات:** تأكد من عمل نسخة احتياطية من الإعدادات والبيانات الخاصة بجهازك قبل البدء في العملية.
- **التحقق من الملفات:** تأكد من أن ملفات الـ Firmware المتاحة متوافقة مع جهازك.
- **اتباع التعليمات:** اتبع التعليمات الخاصة بجهازك والتي قد تكون متاحة في التوثيق الرسمي لـ OpenWrt.

إذا كنت بحاجة إلى توجيهات أكثر تفصيلًا بناءً على نوع الجهاز المحدد لديك، قد يكون من المفيد الرجوع إلى التوثيق الرسمي لـ OpenWrt أو مجتمعات الدعم المختلفة.

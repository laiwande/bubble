$src = 'C:\Users\heale\Desktop\臭'
$dst = 'd:\epan\android_wp\bubble\frontend\app\src\main\res\drawable'

Remove-Item "$dst\symbol_0*.webp" -ErrorAction SilentlyContinue

$files = @(
    '微信图片_20260530212958_379_93.png',
    '微信图片_20260530212959_380_93.png',
    '微信图片_20260530213000_381_93.png',
    '微信图片_20260530213002_382_93.png',
    '微信图片_20260530213003_383_93.png',
    '微信图片_20260530213005_384_93.png',
    '微信图片_20260530213006_385_93.png',
    '微信图片_20260530213007_386_93.png',
    '微信图片_20260530213008_387_93.png',
    '微信图片_20260530213009_388_93.png'
)

for ($i = 0; $i -lt 10; $i++) {
    $num = $i + 1
    $srcPath = Join-Path $src $files[$i]
    $dstPath = Join-Path $dst ("symbol_{0:D2}.png" -f $num)
    Copy-Item $srcPath $dstPath -ErrorAction Stop
    Write-Host "Copied: symbol_$('{0:D2}' -f $num).png"
}
Write-Host 'All done!'

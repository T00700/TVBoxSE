<!DOCTYPE html>
<html>
<body>

<?php 
$myLinks = array("https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/01.png", 
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/02.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/03.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/04.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/05.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/06.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/07.png",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/08.jpg",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/09.jpg",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/10.jpg",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/11.jpg",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/12.jpg",
    "https://agit.ai/00700/vvebo/raw/branch/main/wallpaper/13.jpg");

$randomRedirection = $myLinks[array_rand($myLinks)];
header("Location: $randomRedirection");
?>

</body>
</html>
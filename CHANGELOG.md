## Changelog

This release is primarily spring-cleaning on the codebase to make it easier to update and maintain, given Hypixel's push for modern versions of Minecraft.

- Port to 1.21.11
- Drop versions below 1.21.10
  - Hypixel (SkyBlock) will [only be supporting the two latest game drops](https://hypixel.net/threads/an-update-on-minecraft-versions-and-skyblock-going-forward.6053933), and as 1.21.10 was a hotfix for 1.21.9, we will only be supporting versions 1.21.10 onward.
- Fix rank spoofing in chat messages
- Change default spoofed rank
  - If you didn't know, the [`[OWNER]` rank was removed and replaced](https://hypixel.net/threads/team-update-one-unified-rank.5628609) with a singular "Hypixel Staff" rank, so the default spoofed rank has been updated to use this rank.

---

This is the modern version (`v2`), made for the latest versions of Minecraft. To see the legacy version (`v1`), made for 1.8.9, check out the [latest `v1` release](https://modrinth.com/mod/rankspoof/version/v1.1.2).
## Changelog

- Port to 26.1, 26.2 and 26.3
- Properly handle player tags
  - This fixes an issue with spoofing if the player had any tags, such as guild tags or housing ranks.
- Cache spoofed rank components
  - This should improve performance as components aren't rebuilt every single time the rank gets spoofed.

---

This is the modern version (`v2`), made for the latest versions of Minecraft. To see the legacy version (`v1`), made for 1.8.9, check out the [latest `v1` release](https://modrinth.com/mod/rankspoof/version/v1.2.0).

package dev.hely.tab.api.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.hely.tab.api.Tab;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */

@Getter
@AllArgsConstructor
public class Skin {

    private final String value;
    private final String signature;

    public static final Map<UUID, Skin> CACHE = new ConcurrentHashMap<>();


    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!object.getClass().equals(getClass())) return false;

        Skin skin = (Skin) object;

        return skin.getValue().equals(value) && skin.getSignature().equals(signature);
    }

    public static final Skin STEVE = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYxODI4Nzc3MDYxNSwKICAicHJvZmlsZUlkIiA6ICI4NjY3YmE3MWI4NWE0MDA0YWY1NDQ1N2E5NzM0ZWVkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGV2ZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGE1YmQwMTZiM2M5YTFiOTI3MmU0OTI5ZTMwODI3YTY3YmU0ZWJiMjE5MDE3YWRiYmM0YTRkMjJlYmQ1YjEiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk1M2NhYzhiNzc5ZmU0MTM4M2U2NzVlZTJiODYwNzFhNzE2NThmMjE4MGY1NmZiY2U4YWEzMTVlYTcwZTJlZDYiCiAgICB9CiAgfQp9",
            "dnRZKI5Y443vH8PJASARp6D1RKayS5aAxEg6xkPX0wiIVo94YWiCu86/2NrYeh/Ltm76wHN9lTi/gBfhGY/De70SINNZEgTYAso9G3NsoBmAzQawsMdK5e4IRHvx5jun5sPqGS63Kb4Kuue8VNzsWz1eVOJus6GlN36GFwvO32qrXhhJaEho21MIYa9ySQQua38I3/Du5wfKiC5JerVjr7LP1GXEQvz3OrCP0u0dNs56JlqMjmTIcLmStx4EKJaHks5rfUKZON79TpjyJQkmw2rWOL2357ROpp3dBtH12Tie3MHchwYd7JEtkKx7JVq7kgVjF4yCBJ2ugBH8+Nc/GQij03sK4dASj3S+/o7zdBvWRIOnqNa7KEpP2s3l846NRwSC5YlKBK4hlccmGnlpmBzHlPl4b1vA3jZNG1HBNQ6VHX/LIJRUfvWnEXZVU0b+usTTdTdx5HB1ZYfN4Uekk/ilE5R/UNQ7A1q9EJ/WnbfA/SeZHda4k0he9PJDp52mcHMB0Q+fb1dEgFnxkF1OVHtJsHKi2zplpDtTPejy98d5qvBwVa9Ss2ygc1eoBG6QCcrf7ESTWgNrNPJLdEkahQCDVad6B8UpMq8LLREf2csMQt89XoAgS6n9OZTSLbzUxttvIq+Dkzo9Ae9iz3JlmEIGcBKoAE+9UUTl52L+E8o="
    );

    public static final Skin DEFAULT = new Skin(
            "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
            "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    );


    public static final Skin DISCORD_SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYwNzkxMjY1Nzc4MywKICAicHJvZmlsZUlkIiA6ICI5MWZlMTk2ODdjOTA0NjU2YWExZmMwNTk4NmRkM2ZlNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJoaGphYnJpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzUzN2I0MjMwYjFmMjE2MDU0NTUyODgzYjU1MDQwODg3MDAwNDNmZTgzYmM5ZDY0MjlhMTIyMjUzZjQ3ZjJiIgogICAgfQogIH0KfQ==",
            "as+ENbxPgtwc8Qwy6ek8yFZMsDrHWGhtuB5tfhIUnp0gmr30P3sPErHdLt3srS+mJ5yEv0k2uv5lGHCaynWfi18jkIyp/Q/X8WaN7qiKJpF5MpkHhA48ICkxX90C8dQBxpZ9JZjUXJ+oPuock3oYWpNYdJkDcheQUKGkp19b5G5QXkdb46mtnQlSBU7ZhD/kLUu7KN2jy2+kD5cxzn5Ef2VpRoMi8+WXp3mVq11za8bvg0yvWKyfc2ZiVRvMvVqxi4p6AG5AMbI4EJkYzgt5EW3thqKmg4qAlOCSCli0cmHRuRar0DApd5d73slKf1z7DzVNyG6ak0Y7FcDtkr2vf2j4Iwnxbvh4ksyoh27nJLNSgYXlKgrLOD5VFv4sf97L6LmevTlYRk0wLu+o4Ex1j8E6WJS6vhbYFbCAWpBygHG0KYHq/zq9XetparZFh09d66VpqJd5OdJD+AfTTBpVOTa+guQDkj8p9ReoIolgRz03GJiHqilPYT35jmH7kBNJsfdmGK+2jJnITHjPOF7tNprMyg7a4/Qj/pstR/gNbBq3c6q4/naChGHyEDSo835k8OpCMwUFlNg20bLstsjqJ4Png9RIqlN4AA7hUADm6O5Gz4lnB+mp5m8ZmXrbpBGv2mIEzuvhep/9dWFAJmCtLC+BUW/b/9FUTREqWhw0AXw="
    );


    public static final Skin FACEBOOK_SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTU5ODkzNjE4ODI3MCwKICAicHJvZmlsZUlkIiA6ICI2MTI4MTA4MjU5M2Q0OGQ2OWIzMmI3YjlkMzIxMGUxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJuaWNyb25pYzcyMTk2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM1M2IwZTIyMGYyNzk4YjY4YzRmYzg4ZWY2NDU4MDkxZDcyMDU2ZTFkNGNkOGU4MmRkNjRiNWQ2NTcwZjM1MTQiCiAgICB9CiAgfQp9",
            "aT/m2pZfq9tR1QwcIIgQTm8ytO4BRLXj5eZxZaFEr1k8IK2+utDCtfErrWtA5fNnWrkyj4AspPKT0Cw0WmgsYyqghhKBi9VS5CUCi/o2i8H6K76vFiYO5+5sKJ560RoPC/d2yJ/QS1qa6tbqD+8cIT4Hrh2Ni3ouuEtz1/MPg68p/EqkdNQEzNKE7xnuH9BFX06JLiKql35t/Mk/iMC3WdqCp1SuEmFE5sXEwfR+YpB3fuhmwi6xZj66TaVSZ0WFVkI1j09ROnVAcdhUa/yj4Q/kKBCeV7/4LsBeQhkr2noJh2UFDYPTSpQSgeNBlzgvSMr78CPLIVdpeyQ6MuoiG8HED5vytwJSymvgIf8v94tsDB8s8HfKwWIer7FoZ8kfY9BGhoXw22wCnmkRtzEVB+8wSpIKxwrjYMrSGiTIh71wrLkPi3nBVzy0lAEnQPmm7qePjET8R/l4LWT3kWojqiqS+YwMIXecSWTlhbquI1d8F9mFmDXYh+xSXsD+DqGpVlKuCn9RKXBrtaDIQtXu/5AxryQFgSSJOinxweOwlUAgFmiB6bO20KUfLM3YXdaxK7NNxCPVAoGb3gmvlsSpoGCW7KRobVwVNLQaCSzcXwE7WeRb1+c/xb918+b98eaGj7iIHicrxOMfuXOWEaDHDrrAXVydhEhcS6+3eccVZ84="
    );

    public static final Skin TWITTER_SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYyOTU5MDIwMzEzOCwKICAicHJvZmlsZUlkIiA6ICI0ZWQ4MjMzNzFhMmU0YmI3YTVlYWJmY2ZmZGE4NDk1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJGaXJlYnlyZDg4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UxZGMyZGVjOTBmMzc0ZDZlMDc1YWNmYTE4YzBhMmMwZDc5YWE5Mzk1NjdiZmNkY2ZlMTk2YWVlOGNmMzM0NTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "u+XgMFGg3/tTtD6O6wabu4cRiCY7Y0YwmGVb/6jmIYZAMUP7xySFKzxZVnORjsmNajxdYkngRcJI/jXVU/3RmTBOHLXzUaq/anoL9uaK5AtOER1vLfyynDSlyilRHccBDjpKrl+dx8noIO/Q5fdU9SHMKPy/uZWl73n/V8Kw+uh+JqPyYqArLCKNedrKiyqveh/x4MIJdJx+tRomROezDoOhUsYdM2IXsIEecVPKldeSqL1swZNzPllrOE44DN2+v8tvs/BWhrGxCagD/Pf9Csdjr+RHqj2CJP3fST67uhb6iMr8ItS0s5ki9MBWIUVqSaTSc+0yD9kSeuLs70eammJzEnOWts1P/559r9XgwG8hqbhO/dvj12SBoZOTLre2dbvBrQyzh/g/GK51UB8FQMUnN5T8usC56uXkhMHBfkBtrXLiwMUdomDNyRbB36eAxoNDKR83RjLUsIp9sqeArxidqAtqaFVFSfsXGa7Hjd+4XAaaItmsejhsjFXBbmta8BW6221UdsrLGkYglFlkL9xCsn6PS236/5YAu9paWsoHD1axMbMCHwqJvbkb3OimqiQ9sHXYZKsukStNv3ijsuuJWtp2mnahaRLF1EewDiS15RlUBm6+J507x6iSt8upqO8AXYwSBQ7BQWVLzF3UI5Weuf3Ptc8Fg9W1ikuAWDo="
    );

    public static final Skin YOUTUBE_SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTU5ODk5NjQzNzA0MSwKICAicHJvZmlsZUlkIiA6ICJhNzFjNTQ5MmQwNTE0ZDg3OGFiOTEwZmRmZmRmYzgyZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcHBsZTU0NDciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmExYzQ4YmYwYTIyMzBhNDYxMDMwMDY1ZWI5NDFhZDU2MGQwOGZhZDUwYWU1NjM0OTczZTVhYWVjNDcwZTZiYiIKICAgIH0KICB9Cn0=",
            "ljAxdmighg13rgsWrkIuLmtPF/cNi3nVccEQSJD2FSwGvF7avKDuh9xV1ybM4XdHQVusdFSsdEhofDktszyMauUOvF/GZPxGGG+6WDG1HSPD3JmbUckYdd0608unBP8atA7TThUyP3tncnHhn9MtUbVwdq6WCtSCWw0LejLyyOHz4mSzMjcfbZUkOKhRf6zZ3ZjvLx92UDMiOFJcZ+HBXMIrpFgO2RrJvxJpVQYCFZptdCcPyuwh5Di3LVmy01ZAp4hfxCVOIjpcNMd0UcgjYdmDvGSFkXZty2DaVFma3OeowNZS2ISjG4E3GbBmPBSnctiT60ugrSmFQk2/9oHYeO4cs9cuW4baYOY2yHrNwsTWYjIoqf84wzzlpYxwWNejXYa6ckc3iM9ycgaGeCes3pzG7U01M7qQl85xigg5c0CZFgrjMCdCMwG8cp4uTtNY3TZg+jK7PtYE1QCrq6uIwBeBAN0/Me3lNgKEVKDXrsvtmUvTp5eerlm1lpQGIdF+L6HLV9ZBDWRV+gPEjgeZPdQaq4QmV3/88xs0SGScwdJkT3dRy6fdVnseYLZhSWeqP9cy28vm32JcTcOZMEbc6JBrwmj0POoE8PlllaSotCqW/RGopSiDAzH/tGhKTDX8XDYwK4eQ8jj+8frK1Q8S13wKCpdpGRGpQtZQfG1ntxg="
    );

    public static final Skin STORE_SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYwMTU5NzczMzkyOSwKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjYzJhZDNmYWEwMmYzMzUwMjc3YjYwM2U0ZWI2MTg1ZWFiZDQ3NDM5ZDJkZmQwZjc2MjRlNjg2MDUzZjZhYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "rp4lgczvE1ZOwW1/rhKhtzBvCFz5XolZ7BoEm3bieoDrbjMqvsrjyMDs6KdvxbOQI26JdrdWzJUm5mam47QKDes682dtqVvefD221uDRG9vb6km3Xal2B5sGqdhl9PtuQEeGNZlTI1Ip4kBHBIcugTCWV0NzpG3dTWDi/E7rIKviGRxL4hQXMysY9e8P0de8WOMeQ/X5vV0jkPgc15OLBW+k/VTvPmFGTAv/r0Bp3sRP3L36KB8auDXWpD7DTNgGuJTlDKm1/sjTRO/5+cT/5wK6Q8oxzfrnNgt81syuCwLVpt1SP18zLUdKEGTlsSV/01HLCrQusYyBwu5gxyXkpNnd2+bg4DA+DlgxApssZEYhKKR5UsgQwrcG8GU6O1Gxt73ZSjtkq31Wa2J6+RXYoFnBQC6apIcFHoRGk7FW03x7mZiGauCCNhCIJ6gmCMjml9wwbrFP3lLedIQRn+9NgckvKOtS6dCzwyf/9+A3Fl3GlPqQRhVq96MLDnA28gcYWKToDqgh/Ra0MDJ3alSEBf5MY3ayOz3EUivxQR1ClMu6i3D+aAawqGUc3pbcrNlrlvffBfMAgGc7nTvtWakloqWlE/Xu4CcZ/fK0BIFGf3MD77CRJHF/MfVQFYkkGyuh5zbd/qEmrKaCKvxavopFallTr89zasWbg51nlhA7lgU="
    );



    public static Skin getSkin(UUID uniqueId) {
        if (CACHE.containsKey(uniqueId)) return CACHE.get(uniqueId);

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString().replace("-", "") + "?unsigned=false");
            JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            return CACHE.put(uniqueId, new Skin(json.get("value").getAsString(), json.get("signature").getAsString()));
        } catch (IOException ignored) {
            // ignored
        }

        return STEVE;
    }

    public static Skin getSkin(Player player) {
        return Tab.getInstance().getTabNMS().getSkin(player);
    }
}

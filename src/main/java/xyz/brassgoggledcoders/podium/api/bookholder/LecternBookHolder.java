package xyz.brassgoggledcoders.podium.api.bookholder;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraftforge.common.MinecraftForge;
import xyz.brassgoggledcoders.podium.api.PodiumAPI;
import xyz.brassgoggledcoders.podium.api.event.GetPageContentsEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Optional;

public class LecternBookHolder implements IBookHolder {
    private final WeakReference<LecternTileEntity> lectern;
    private boolean gettingContents = false;

    public LecternBookHolder(LecternTileEntity lectern) {
        this.lectern = new WeakReference<>(lectern);
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return Optional.ofNullable(lectern.get())
                .map(LecternTileEntity::getBook)
                .orElse(ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public String getOpenPage() {
        return Optional.ofNullable(lectern.get())
                .map(LecternTileEntity::getPage)
                .orElse(0) + "";
    }

    @Nullable
    @Override
    public String getPageContents() {
        if (!this.gettingContents) {
            this.gettingContents = true;
            GetPageContentsEvent getPageContentsEvent = new GetPageContentsEvent(this);
            MinecraftForge.EVENT_BUS.post(getPageContentsEvent);
            this.gettingContents = false;
            return getPageContentsEvent.getPageContent();
        } else {
            PodiumAPI.LOGGER.error("Called 'getPageContents' from 'BookContentsEvent'");
            return null;
        }
    }
}

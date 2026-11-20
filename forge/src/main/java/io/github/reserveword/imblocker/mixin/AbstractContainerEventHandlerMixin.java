package io.github.reserveword.imblocker.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

@Mixin(AbstractContainerEventHandler.class)
public abstract class AbstractContainerEventHandlerMixin {
	
	@Shadow
	@Nullable
	private GuiEventListener focused;
	
	@Overwrite
	public void setFocused(@Nullable GuiEventListener focused) {
		if(this.focused != focused) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (focused != null) {
				focused.setFocused(true);
			}

			this.focused = focused;
		} else if (focused != null && !focused.isFocused()) {
			// A widget can drop its own focus (Refined Storage's grid search box does on Enter or an
			// outside click) while this screen still points at it; clicking it again must re-focus it.
			focused.setFocused(true);
		}
	}
}

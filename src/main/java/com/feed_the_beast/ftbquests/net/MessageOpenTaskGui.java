package com.feed_the_beast.ftbquests.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.gui.ClientQuestFile;
import com.feed_the_beast.ftbquests.gui.ContainerTask;
import com.feed_the_beast.ftbquests.gui.GuiTask;
import com.feed_the_beast.ftbquests.quest.tasks.QuestTaskData;
import com.feed_the_beast.ftbquests.tile.TileScreenBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageOpenTaskGui extends MessageToClient
{
	private short task;
	private int window;
	private boolean hasPos;
	private BlockPos pos;

	public MessageOpenTaskGui()
	{
	}

	public MessageOpenTaskGui(short t, int w, boolean h, @Nullable BlockPos p)
	{
		task = t;
		window = w;
		hasPos = h;
		pos = p;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBQuestsNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeShort(task);
		data.writeInt(window);
		data.writeBoolean(hasPos);

		if (hasPos)
		{
			data.writePos(pos);
		}
	}

	@Override
	public void readData(DataIn data)
	{
		task = data.readShort();
		window = data.readInt();
		hasPos = data.readBoolean();

		if (hasPos)
		{
			pos = data.readPos();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		QuestTaskData data = ClientQuestFile.INSTANCE.getQuestTaskData(task);

		if (data.task.quest.canStartTasks(ClientQuestFile.INSTANCE))
		{
			ContainerTask container = new ContainerTask(ClientUtils.MC.player, data);
			container.windowId = window;

			if (hasPos)
			{
				TileEntity tileEntity = ClientUtils.MC.world.getTileEntity(pos);

				if (tileEntity instanceof TileScreenBase)
				{
					container.screen = ((TileScreenBase) tileEntity).getScreen();
				}
			}

			new GuiTask(container).openGui();
		}
	}
}
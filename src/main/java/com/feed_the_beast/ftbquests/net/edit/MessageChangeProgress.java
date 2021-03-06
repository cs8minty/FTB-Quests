package com.feed_the_beast.ftbquests.net.edit;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.ftbquests.quest.EnumChangeProgress;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageChangeProgress extends MessageToServer
{
	private short team;
	private int id;
	private EnumChangeProgress type;

	public MessageChangeProgress()
	{
	}

	public MessageChangeProgress(short t, int i, EnumChangeProgress ty)
	{
		team = t;
		id = i;
		type = ty;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBQuestsEditNetHandler.EDIT;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeShort(team);
		data.writeInt(id);
		data.write(type, EnumChangeProgress.NAME_MAP);
	}

	@Override
	public void readData(DataIn data)
	{
		team = data.readShort();
		id = data.readInt();
		type = EnumChangeProgress.NAME_MAP.read(data);
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (FTBQuests.canEdit(player))
		{
			QuestObject object = ServerQuestFile.INSTANCE.get(id);

			if (object != null)
			{
				ITeamData t = ServerQuestFile.INSTANCE.getData(team);

				if (t != null)
				{
					EnumChangeProgress.sendUpdates = false;
					object.changeProgress(t, type);
					EnumChangeProgress.sendUpdates = true;
					new MessageChangeProgressResponse(team, id, type).sendToAll();
				}
			}
		}
	}
}